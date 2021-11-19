package com.example.monster.sampleproject.Service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.monster.sampleproject.Activities.MainActivity;
import com.example.monster.sampleproject.Interface.IPlayer;
import com.example.monster.sampleproject.R;
import com.example.monster.sampleproject.Singleton.Player;
import com.google.android.exoplayer.ExoPlaybackException;

public class NotificationService extends Service {

    private Player player;
    private String url;
    private String content;

    public NotificationService() {
        /*mediaPlayer = new MediaPlayer();*/
        player = Player.getInstance(new IPlayer() {

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }

            @Override
            public void onPlayWhenReadyCommitted() {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();

        if (extras != null) {
            url = extras.getString("url");
            content = extras.getString("content");
        } else {
            Log.d("TEMP", "Extras are NULL");
        }

        if (intent.getAction().equals("Start")) {
            showNotification(getApplicationContext());
        } else if (intent.getAction().equals("Pause")) {
            player.stop();
            System.exit(0);
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    private void showNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "channel-01";
        String channelName = "İnternet Radyo Demo";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notify_player);

        Intent intt = new Intent(this, NotificationService.class);
        intt.setAction("Pause");
        PendingIntent playIntent = PendingIntent.getService(this, 0, intt, 0);

        contentView.setOnClickPendingIntent(R.id.btnplay, playIntent);

        contentView.setTextViewText(R.id.notificationContent, content);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(false)
                .setCustomContentView(contentView);

        player.start(url, context);
        startForeground(100, mBuilder.build());
    }
}