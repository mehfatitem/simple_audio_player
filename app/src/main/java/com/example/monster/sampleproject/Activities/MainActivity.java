package com.example.monster.sampleproject.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
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

import com.example.monster.sampleproject.Entities.MessageClass;
import com.example.monster.sampleproject.Entities.Post;
import com.example.monster.sampleproject.Entities.Root;
import com.example.monster.sampleproject.Helper.ActivityHelper;
import com.example.monster.sampleproject.Interface.IPlayer;
import com.example.monster.sampleproject.R;
import com.example.monster.sampleproject.Requesthandler.RequestHandler;
import com.example.monster.sampleproject.Service.NotificationService;
import com.example.monster.sampleproject.Singleton.Player;
import com.google.android.exoplayer.ExoPlaybackException;
import com.google.gson.Gson;
import java.util.ArrayList;
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
    private List<String> streamAddressList;
    private ProgressDialog progressDialog;
    private ActivityHelper ah = new ActivityHelper();
    private MessageClass mc = new MessageClass();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!ah.isOnline(MainActivity.this)) {
                ah.alertBox(MainActivity.this, mc.getUsername(), mc.getInternetConnectionStatus());
            }
        }
    };

    private IntentFilter filters;
    private Button notiButton;
    private int clickCount = 0;
    private Player player;


    @SuppressWarnings("unchecked")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        hideStatusBar();
        setContentView(R.layout.activity_main);

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

        footerText.setText(mc.getAllRightReserved() + year + " | " + mc.getUsername());


        loadingText.setVisibility(View.VISIBLE);

        playPauseButton.setImageResource(R.drawable.play2);

        playPauseButton.performClick();

        radioLiveTitle.setText(mc.getSelectedRadio());

        try {
            new RequestAsyncGet().execute();
        } catch (Exception ex) {

        } finally {

            radioLiveKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    progressDialog.setMessage(mc.getLoading());
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
        }

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    if (!ah.isOnline(MainActivity.this)) {

                        ah.alertBox(MainActivity.this, mc.getWarning(),  mc.getInternetConnectionStatus());
                        playPauseButton.setImageResource(R.drawable.play2);
                        loadingText.setText(mc.getStopped());
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
                            loadingText.setText(mc.getStopped());
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

    public class RequestAsyncGet extends AsyncTask<String,String,String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                String radioInfoJson =  RequestHandler.sendGet(mc.getRadioInfoServiceUrl());

                return radioInfoJson;
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                Root radioInfoRoot = new Root();
                try{
                    radioInfoRoot  = new Gson().fromJson(s, Root.class);
                }catch (Exception ex) {
                    s = ah.loadJSONFromAsset(MainActivity.this , "radio_info.json");
                    radioInfoRoot  = new Gson().fromJson(s, Root.class);
                }


                itemList = new ArrayList<String>();
                streamAddressList = new ArrayList<String>();

                for (Post item : radioInfoRoot.posts) {
                    itemList.add(item.radio_name);
                    streamAddressList.add(item.stream_url);
                }

                adapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        itemList);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                radioLiveKindSpinner.setAdapter(adapter);
            }
        }
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


