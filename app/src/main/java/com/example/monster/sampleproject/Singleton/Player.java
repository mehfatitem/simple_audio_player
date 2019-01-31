package com.example.monster.sampleproject.Singleton;

import android.content.Context;
import android.net.Uri;

import com.example.monster.sampleproject.Interface.IPlayer;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.BandwidthMeter;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import okhttp3.OkHttpClient;

public class Player {
    public static Player instance = null;

    public static boolean btnStatus = false;

    private ExoPlayer exoPlayer;

    private Player(final IPlayer iplayer) {
        exoPlayer = ExoPlayer.Factory.newInstance(1);

        exoPlayer.addListener(new ExoPlayer.Listener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                iplayer.onPlayerStateChanged(playWhenReady, playbackState);
            }

            @Override
            public void onPlayWhenReadyCommitted() {
                iplayer.onPlayWhenReadyCommitted();
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                iplayer.onPlayerError(error);
            }
        });
    }

    public static Player getInstance(IPlayer iplayer) {
        if (instance == null) {
            instance = new Player(iplayer);
        }

        return instance;
    }

    public void start(String url, Context context) {
        if (exoPlayer != null) {
            exoPlayer.stop();
        }

        int BUFFER_SEGMENT_SIZE = 64 * 1024;
        int BUFFER_SEGMENT_COUNT = 256;

        Uri radioUri = Uri.parse(url);

        Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
        String userAgent = Util.getUserAgent(context, "Exo Player");

        BandwidthMeter bandwidthMeter = new BandwidthMeter() {
            @Override
            public long getBitrateEstimate() {
                return 0;
            }

            @Override
            public void onTransferStart() {

            }

            @Override
            public void onBytesTransferred(int bytesTransferred) {

            }

            @Override
            public void onTransferEnd() {

            }
        };

        OkHttpClient okHttpClient = new OkHttpClient();
        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, new OkHttpDataSource(okHttpClient, userAgent, null, bandwidthMeter));
        ExtractorSampleSource sampleSource = new ExtractorSampleSource(radioUri, dataSource, allocator, BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);
        MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
        exoPlayer.prepare(audioRenderer);

        exoPlayer.setPlayWhenReady(true);
    }

    public void stop() {
        exoPlayer.stop();
    }

    public void release() {
        exoPlayer.stop();
        exoPlayer.release();
    }
}
