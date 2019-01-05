package com.example.monster.sampleproject;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private  ImageView playPauseButton;
    private TextView loadingText;
    private String radioStreamUrl = "https://listen.powerapp.com.tr/powerturk/mpeg/icecast.audio?/;stream.mp3";

    private int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        playPauseButton =  (ImageView) findViewById(R.id.playPauseButton);
        loadingText = (TextView) findViewById(R.id.loadingText);

        loadingText.setVisibility(View.VISIBLE);

        playPauseButton.setImageResource(R.drawable.play);

        playPauseButton.performClick();


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            MediaPlayer mediaPlayer;

            public void onClick(View v) {
                if(clickCount % 2 == 0){
                    playPauseButton.setImageResource(R.drawable.pause);
                    loadingText.setVisibility(View.VISIBLE);
                    try{

                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource( MainActivity.this , Uri.parse(MainActivity.this.radioStreamUrl));
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.prepare(); //don't use prepareAsync for mp3 playback
                        mediaPlayer.start();
                    }catch(Exception ex){
                        Toast.makeText(MainActivity.this, ex.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    finally {
                        loadingText.setVisibility(View.INVISIBLE);

                    }

                }else{
                    playPauseButton.setImageResource(R.drawable.play);
                    loadingText.setVisibility(View.VISIBLE);
                    loadingText.setText("Durduruldu ...");

                    stopPlaying();
                }
                clickCount++;
            }



            private void stopPlaying() {
                try{
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }catch (Exception ex){
                    Toast.makeText(MainActivity.this, ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
