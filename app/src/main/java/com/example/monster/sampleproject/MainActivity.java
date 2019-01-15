package com.example.monster.sampleproject;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ImageView playPauseButton;
    private TextView radioLiveTitle;
    private TextView loadingText;
    private TextView footerText;
    private String radioStreamUrl = "";
    private Spinner radioLiveKindSpinner;
    private ArrayAdapter adapter;
    private String tmpLiveName;
    private boolean isPlaying = false;
    private List<String> itemList;
    private ProgressDialog progressDialog;
    private ActivityHelper ah = new ActivityHelper();
    private BroadcastReceiver receiver;
    private IntentFilter filters;
    private int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        hideStatusBar();
        setContentView(R.layout.activity_main);


        radioLiveKindSpinner = (Spinner) findViewById(R.id.radioLiveSpinner);
        radioLiveTitle = (TextView) findViewById(R.id.radioLiveTitle);
        playPauseButton = (ImageView) findViewById(R.id.playPauseButton);
        loadingText = (TextView) findViewById(R.id.loadingText);
        footerText = (TextView) findViewById(R.id.footerText);
        progressDialog = new ProgressDialog(MainActivity.this);

        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));

        footerText.setText("Tüm Hakları Saklıdır © " + year + " | mehfatitem");


        loadingText.setVisibility(View.VISIBLE);

        playPauseButton.setImageResource(R.drawable.play);

        playPauseButton.performClick();

        radioLiveTitle.setText("Radyo Yayını Seçiniz...");

        itemList = new ArrayList<String>();
        itemList.add("Polis Radyosu U.Y.");
        itemList.add("Polis Radyosu TSM Y.");
        itemList.add("Power Türk");
        itemList.add("Virgin Radio");
        itemList.add("Radio Fenomen");
        itemList.add("7/24 Türkçe Rap");
        itemList.add("80'ler Gold");
        itemList.add("Fenomen Rap");
        itemList.add("Arabesk FM");
        itemList.add("Metro FM");


        adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                itemList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        radioLiveKindSpinner.setAdapter(adapter);

        radioLiveKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressDialog.setMessage("Yükleniyor..");
                if (i == 0) {
                    radioStreamUrl = "https://m.egm.gov.tr:8095";
                } else if (i == 1) {
                    radioStreamUrl = "https://m.egm.gov.tr:8095";
                } else if (i == 2) {
                    radioStreamUrl = "https://listen.powerapp.com.tr/powerturk/mpeg/icecast.audio?/;stream.mp3";
                } else if (i == 3) {
                    radioStreamUrl = "http://vr-live-mp3-128.scdn.arkena.com/virginradio.mp3";
                } else if (i == 4) {
                    radioStreamUrl = "https://listen.radyofenomen.com/fenomen/128/icecast.audio";
                } else if(i == 5) {
                    radioStreamUrl = "http://95.173.188.166:9984/";
                } else if(i == 6) {
                    radioStreamUrl = "https://17773.live.streamtheworld.com/FLASHBACK.mp3";
                } else if(i == 7) {
                    radioStreamUrl = "http://fenomenoriental.listenfenomen.com/fenomenrap/128/icecast.audio";
                } else if(i == 8) {
                    radioStreamUrl = "http://yayin.damarfm.com:8080/mp3";
                } else if(i == 9) {
                    radioStreamUrl = "https://17753.live.streamtheworld.com/METRO_FM.mp3";
                }
                if (!isPlaying) {
                    playPauseButton.performClick();
                } else {
                    playPauseButton.performClick();
                    playPauseButton.performClick();
                }
                tmpLiveName = itemList.get(i);
                loadingText.setText(itemList.get(i));
                Log.d("stream url", radioStreamUrl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });


        playPauseButton.setOnClickListener(new View.OnClickListener() {
            MediaPlayer mediaPlayer;
            public void onClick(View v) {
                hideStatusBar();
                if (!ah.isOnline(MainActivity.this)) {

                    ah.alertBox(MainActivity.this, "Uyarı", "İnternet bağlantısını aktif hale getiriniz!");
                    playPauseButton.setImageResource(R.drawable.play);
                    loadingText.setText("Durduruldu ...");
                    stopPlaying();
                } else {
                    if (clickCount % 2 == 0) {
                        playPauseButton.setImageResource(R.drawable.pause);
                        try {
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
                            mediaPlayer.setDataSource(MainActivity.this, Uri.parse(MainActivity.this.radioStreamUrl));
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.prepare(); //don't use prepareAsync for mp3 playback
                            mediaPlayer.start();
                        } catch (Exception ex) {
                            Toast.makeText(MainActivity.this, ex.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            mediaPlayer = new MediaPlayer();
                            isPlaying = false;
                        } finally {
                            loadingText.setText(tmpLiveName);

                        }
                        isPlaying = true;
                    } else {
                        playPauseButton.setImageResource(R.drawable.play);
                        loadingText.setText("Durduruldu ...");
                        stopPlaying();
                    }
                    clickCount++;
                }
            }


            private void stopPlaying() {
                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                        isPlaying = false;
                    }
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }

        });
    }


    private void hideStatusBar() {
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                    | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // hide status bar and nav bar after a short delay, or if the user interacts with the middle of the screen
            );
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        //registerReceiver(receiver, filters);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(receiver);
    }
}


