package com.example.monster.sampleproject.Singleton;

import com.google.android.exoplayer.C;
import com.google.android.exoplayer.upstream.DataSpec;
import com.google.android.exoplayer.upstream.HttpDataSource;
import com.google.android.exoplayer.upstream.TransferListener;
import com.google.android.exoplayer.util.Assertions;
import com.google.android.exoplayer.util.Predicate;

import okhttp3.CacheControl;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class OkHttpDataSource implements HttpDataSource {
    private static final AtomicReference<byte[]> skipBufferReference = new AtomicReference<>();

    private final OkHttpClient okHttpClient;
    private final String userAgent;
    private final Predicate<String> contentTypePredicate;
    private final TransferListener listener;
    private final CacheControl cacheControl;
    private final HashMap<String, String> requestProperties;

    private DataSpec dataSpec;
    private Response response;
    private InputStream responseByteStream;
    private boolean opened;

    private long bytesToSkip;
    private long bytesToRead;

    private long bytesSkipped;
    private long bytesRead;

    public OkHttpDataSource(OkHttpClient client, String userAgent, Predicate<String> contentTypePredicate) {
        this(client, userAgent, contentTypePredicate, null);
    }

    public OkHttpDataSource(OkHttpClient client, String userAgent, Predicate<String> contentTypePredicate, TransferListener listener) {
        this(client, userAgent, contentTypePredicate, listener, null);
    }

    public OkHttpDataSource(OkHttpClient client, String userAgent, Predicate<String> contentTypePredicate, TransferListener listener, CacheControl cacheControl) {
        this.okHttpClient = Assertions.checkNotNull(client);
        this.userAgent = Assertions.checkNotEmpty(userAgent);
        this.contentTypePredicate = contentTypePredicate;
        this.listener = listener;
        this.cacheControl = cacheControl;
        this.requestProperties = new HashMap<>();
    }

    @Override
    public String getUri() {
        return response == null ? null : response.request().url().toString();
    }

    @Override
    public Map<String, List<String>> getResponseHeaders() {
        return response == null ? null : response.headers().toMultimap();
    }

    @Override
    public void setRequestProperty(String name, String value) {
        Assertions.checkNotNull(name);
        Assertions.checkNotNull(value);

        synchronized (requestProperties) {
            requestProperties.put(name, value);
        }
    }

    @Override
    public void clearRequestProperty(String name) {
        Assertions.checkNotNull(name);
        synchronized (requestProperties) {
            requestProperties.remove(name);
        }
    }

    @Override
    public void clearAllRequestProperties() {
        synchronized (requestProperties) {
            requestProperties.clear();
        }
    }

    @Override
    public long open(DataSpec dataSpec) throws HttpDataSourceException {
        this.dataSpec = dataSpec;
        this.bytesRead = 0;
        this.bytesSkipped = 0;
        Request request = makeRequest(dataSpec);

        try {
            response = okHttpClient.newCall(request).execute();
            responseByteStream = response.body().byteStream();
        } catch (IOException e) {
            throw new HttpDataSourceException("Unable to connect to " + dataSpec.uri.toString(), e, dataSpec);
        }

        int responseCode = response.code();

        if (!response.isSuccessful()) {
            Map<String, List<String>> headers = request.headers().toMultimap();
            closeConnectionQuietly();
            throw new InvalidResponseCodeException(responseCode, headers, dataSpec);
        }

        String contentType = response.body().contentType().toString();

        if (contentTypePredicate != null && !contentTypePredicate.evaluate(contentType)) {
            closeConnectionQuietly();
            throw new InvalidContentTypeException(contentType, dataSpec);
        }

        bytesToSkip = responseCode == 200 && dataSpec.position != 0 ? dataSpec.position : 0;

        long contentLength = response.body().contentLength();

        bytesToRead = dataSpec.length != C.LENGTH_UNBOUNDED ? dataSpec.length : contentLength != -1 ? contentLength - bytesToSkip : C.LENGTH_UNBOUNDED;

        opened = true;

        if (listener != null) {
            listener.onTransferStart();
        }

        return bytesToRead;
    }

    @Override
    public int read(byte[] buffer, int offset, int readLength) throws HttpDataSourceException {
        try {
            skipInternal();
            return readInternal(buffer, offset, readLength);
        } catch (IOException e) {
            throw new HttpDataSourceException(e, dataSpec);
        }
    }

    @Override
    public void close() throws HttpDataSourceException {
        if (opened) {
            opened = false;

            if (listener != null) {
                listener.onTransferEnd();
            }

            closeConnectionQuietly();
        }
    }

    protected final long bytesSkipped() {
        return bytesSkipped;
    }

    protected final long bytesRead() {
        return bytesRead;
    }

    protected final long bytesRemaining() {
        return bytesToRead == C.LENGTH_UNBOUNDED ? bytesToRead : bytesToRead - bytesRead;
    }

    private Request makeRequest(DataSpec dataSpec) {
        long position = dataSpec.position;
        long length = dataSpec.length;
        boolean allowGzip = (dataSpec.flags & DataSpec.FLAG_ALLOW_GZIP) != 0;

        HttpUrl url = HttpUrl.parse(dataSpec.uri.toString());
        Request.Builder builder = new Request.Builder().url(url);

        if (cacheControl != null) {
            builder.cacheControl(cacheControl);
        }

        synchronized (requestProperties) {
            for (Map.Entry<String, String> property : requestProperties.entrySet()) {
                builder.addHeader(property.getKey(), property.getValue());
            }
        }

        if (!(position == 0 && length == C.LENGTH_UNBOUNDED)) {
            String rangeRequest = "bytes=" + position + "-";
            if (length != C.LENGTH_UNBOUNDED) {
                rangeRequest += (position + length - 1);
            }

            builder.addHeader("Range", rangeRequest);
        }
        builder.addHeader("User-Agent", userAgent);

        if (!allowGzip) {
            builder.addHeader("Accept-Encoding", "identity");
        }

        if (dataSpec.postBody != null) {
            builder.post(RequestBody.create(null, dataSpec.postBody));
        }

        return builder.build();
    }

    private void skipInternal() throws IOException {
        if (bytesSkipped == bytesToSkip) {
            return;
        }

        byte[] skipBuffer = skipBufferReference.getAndSet(null);

        if (skipBuffer == null) {
            skipBuffer = new byte[4096];
        }

        while (bytesSkipped != bytesToSkip) {
            int readLength = (int) Math.min(bytesToSkip - bytesSkipped, skipBuffer.length);
            int read = responseByteStream.read(skipBuffer, 0, readLength);

            if (Thread.interrupted()) {
                throw new InterruptedIOException();
            }

            if (read == -1) {
                throw new EOFException();
            }

            bytesSkipped += read;

            if (listener != null) {
                listener.onBytesTransferred(read);
            }
        }

        skipBufferReference.set(skipBuffer);
    }

    private int readInternal(byte[] buffer, int offset, int readLength) throws IOException {
        readLength = bytesToRead == C.LENGTH_UNBOUNDED ? readLength : (int) Math.min(readLength, bytesToRead - bytesRead);

        if (readLength == 0) {
            return C.RESULT_END_OF_INPUT;
        }

        int read = responseByteStream.read(buffer, offset, readLength);

        if (read == -1) {
            if (bytesToRead != C.LENGTH_UNBOUNDED && bytesToRead != bytesRead) {
                throw new EOFException();
            }

            return C.RESULT_END_OF_INPUT;
        }

        bytesRead += read;

        if (listener != null) {
            listener.onBytesTransferred(read);
        }

        return read;
    }

    private void closeConnectionQuietly() {
        response.body().close();
        response = null;
        responseByteStream = null;
    }
}