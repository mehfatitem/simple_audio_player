package com.example.monster.sampleproject.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.monster.sampleproject.Helper.ActivityHelper;
import com.example.monster.sampleproject.Interface.IPlayer;
import com.example.monster.sampleproject.R;
import com.example.monster.sampleproject.Service.NotificationService;
import com.example.monster.sampleproject.Singleton.Player;
import com.google.android.exoplayer.ExoPlaybackException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
    private List<String> streamAddressList;
    private ProgressDialog progressDialog;
    private ActivityHelper ah = new ActivityHelper();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ah.isOnline(MainActivity.this)) {
                ah.alertBox(MainActivity.this, "Uyarı", "İnternet bağlantınızı aktif hale getiriniz!");
            }
        }
    };

    private IntentFilter filters;
    private Button notiButton;
    private int clickCount = 0;
    private Player player;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        hideStatusBar();
        setContentView(R.layout.activity_main);

        //endregion

        radioLiveKindSpinner = (Spinner) findViewById(R.id.radioLiveSpinner);
        radioLiveTitle = (TextView) findViewById(R.id.radioLiveTitle);
        playPauseButton = (ImageView) findViewById(R.id.playPauseButton);
        loadingText = (TextView) findViewById(R.id.loadingText);
        footerText = (TextView) findViewById(R.id.footerText);
        //notiButton =  (Button) findViewById(R.id.notificationClick);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        Calendar calendar = Calendar.getInstance();
        String year = Integer.toString(calendar.get(Calendar.YEAR));

        footerText.setText("Tüm Hakları Saklıdır © " + year + " | mehfatitem");


        loadingText.setVisibility(View.VISIBLE);

        playPauseButton.setImageResource(R.drawable.play2);

        playPauseButton.performClick();

        radioLiveTitle.setText("Radyo Yayını Seçiniz...");

        itemList = new ArrayList<String>();
        itemList.add("Polis Radyosu U.Y.");
        itemList.add("Polis Radyosu TSM Y.");
        itemList.add("Power Türk");
        itemList.add("Virgin Radio");
        itemList.add("Radio Fenomen");
        itemList.add("7/24 Türkçe Rap");
        itemList.add("Fenomen Rap");
        itemList.add("Arabesk FM");
        itemList.add("Metro FM");
        itemList.add("Best FM");
        itemList.add("Kral FM");
        itemList.add("Power FM");

        Collections.sort(itemList);

        streamAddressList = new ArrayList<String>();
        streamAddressList.add("http://95.173.188.166:9984/"); // 7/24 türkçe rap
        streamAddressList.add("http://yayin.damarfm.com:8080/mp3");// arabesk fm
        streamAddressList.add("https://bestfm.turkhosted.com/");// best fm
        streamAddressList.add("https://listen.radyofenomen.com/fenomenrap/128/icecast.audio"); // fenomen rap
        streamAddressList.add("http://46.20.3.204:80/"); // kral fm
        streamAddressList.add("https://25433.live.streamtheworld.com/METRO_FM128AAC.aac");// metro fm
        streamAddressList.add("https://m.egm.gov.tr:8095"); // polis radyosu tsm
        streamAddressList.add("https://m.egm.gov.tr:8093"); // polis radyosu
        streamAddressList.add("http://powerfm.listenpowerapp.com/powerfm/mpeg/icecast.audio");// power fm
        streamAddressList.add("https://listen.powerapp.com.tr/powerturk/mpeg/icecast.audio?/;stream.mp3"); // power turk
        streamAddressList.add("https://listen.radyofenomen.com/fenomen/128/icecast.audio"); // radio fenomen
        streamAddressList.add("https://21323.live.streamtheworld.com/VIRGIN_RADIO.mp3"); // virgin


        adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,
                itemList);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        radioLiveKindSpinner.setAdapter(adapter);

        radioLiveKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressDialog.setMessage("Yükleniyor..");
                radioStreamUrl = streamAddressList.get(i);

                if (!isPlaying) {
                    playPauseButton.performClick();
                } else {
                    playPauseButton.performClick();
                    playPauseButton.performClick();
                }
                tmpLiveName = itemList.get(i);
                loadingText.setText(itemList.get(i));
                Log.d("stream url", radioStreamUrl);
                startService(radioStreamUrl, tmpLiveName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }

        });

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!ah.isOnline(MainActivity.this)) {

                        ah.alertBox(MainActivity.this, "Uyarı", "İnternet bağlantısını aktif hale getiriniz!");
                        playPauseButton.setImageResource(R.drawable.play2);
                        loadingText.setText("Durduruldu ...");
                        stopPlaying();
                    } else {
                        player = Player.getInstance(new IPlayer() {
                            @Override
                            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                                if (playbackState == 2) {
                                    Log.d("Status", "Buffering");
                                    progressDialog.show();
                                }

                                if (playbackState == 4) {
                                    Log.d("Status", "Playing");
                                    Player.btnStatus = true;
                                    progressDialog.hide();
                                }
                            }

                            @Override
                            public void onPlayWhenReadyCommitted() {

                            }

                            @Override
                            public void onPlayerError(ExoPlaybackException error) {

                            }

                        });

                        if (clickCount % 2 == 0) {
                            playPauseButton.setImageResource(R.drawable.pause2);
                            try {
                                progressDialog.show();
                                player.start(radioStreamUrl, MainActivity.this);
                            } catch (Exception ex) {
                                Toast.makeText(MainActivity.this, ex.getMessage(),
                                        Toast.LENGTH_LONG).show();
                                isPlaying = false;
                                progressDialog.hide();
                            } finally {
                                loadingText.setText(tmpLiveName);
                                progressDialog.hide();
                            }
                            isPlaying = true;
                        } else {
                            playPauseButton.setImageResource(R.drawable.play2);
                            loadingText.setText("Durduruldu ...");
                            stopPlaying();
                        }
                        clickCount++;
                    }
                } catch(Exception ex) {
                    Toast.makeText(MainActivity.this, ex.getMessage(),
                            Toast.LENGTH_LONG).show();
                    progressDialog.hide();
                }
            }


            private void stopPlaying() {
                try {
                    if (player != null) {
                        player.stop();
                        isPlaying = false;
                    }
                } catch (Exception ex) {
                    Toast.makeText(MainActivity.this, ex.getMessage(),
                            Toast.LENGTH_LONG).show();

                    progressDialog.hide();
                }
            }

        });
    }

    private void hideStatusBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.getApplicationContext().registerReceiver(receiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        //registerReceiver(receiver, filters);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(receiver);
    }


    public void startService(String url, String context) {
        Intent serviceIntent = new Intent(MainActivity.this, NotificationService.class);
        serviceIntent.setAction("Start");
        serviceIntent.putExtra("url", url);
        serviceIntent.putExtra("content", context);
        startService(serviceIntent);
    }


}


