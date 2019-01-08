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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private  ImageView playPauseButton;
    private TextView radioLiveTitle;
    private TextView loadingText;
    private String radioStreamUrl = "";
    private Spinner radioLiveKindSpinner;
    private ArrayAdapter adapter;

    private String tmpLiveName;
    private boolean isPlaying = false;

    private List<String> itemList;

    ProgressDialog progressDialog;

    private int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        radioLiveKindSpinner = (Spinner) findViewById(R.id.radioLiveSpinner);
        radioLiveTitle = (TextView) findViewById(R.id.radioLiveTitle);
        playPauseButton =  (ImageView) findViewById(R.id.playPauseButton);
        loadingText = (TextView) findViewById(R.id.loadingText);
        progressDialog = new ProgressDialog(MainActivity.this);

        loadingText.setVisibility(View.VISIBLE);

        playPauseButton.setImageResource(R.drawable.play);

        playPauseButton.performClick();

        radioLiveTitle.setText("Radyo Yayını Seçiniz...");

        itemList =
                new ArrayList<String>();
        itemList.add("Ulusal Yayın");
        itemList.add("Türk Sanat Müziği Yayını");
        itemList.add("Power Türk");
        itemList.add("Virgin Radio");
        itemList.add("Radio Fenomen");

        adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                itemList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        radioLiveKindSpinner.setAdapter(adapter);

        radioLiveKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressDialog.setMessage("Yükleniyor..");
                if(i == 0){
                    radioStreamUrl = "https://m.egm.gov.tr:8093";
                } else if(i == 1) {
                    radioStreamUrl = "https://m.egm.gov.tr:8095";
                } else if(i == 2) {
                    radioStreamUrl = "https://listen.powerapp.com.tr/powerturk/mpeg/icecast.audio?/;stream.mp3";
                } else if(i == 3) {
                    radioStreamUrl = "http://vr-live-mp3-128.scdn.arkena.com/virginradio.mp3";
                }
                else if(i == 4) {
                    radioStreamUrl = "https://listen.radyofenomen.com/fenomen/128/icecast.audio";
                }
                if(!isPlaying) {
                    playPauseButton.performClick();
                } else {
                    playPauseButton.performClick();
                    playPauseButton.performClick();
                }
                tmpLiveName = itemList.get(i);
                loadingText.setText(itemList.get(i));
                Log.d("stream url" , radioStreamUrl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            MediaPlayer mediaPlayer;

            public void onClick(View v) {
                if(clickCount % 2 == 0){
                    playPauseButton.setImageResource(R.drawable.pause);
                    try{
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.hide();
                            }
                        }, 1000);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
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
                        loadingText.setText(tmpLiveName);

                    }
                    isPlaying = true;
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
                        isPlaying = false;
                    }
                }catch (Exception ex){
                    Toast.makeText(MainActivity.this, ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
