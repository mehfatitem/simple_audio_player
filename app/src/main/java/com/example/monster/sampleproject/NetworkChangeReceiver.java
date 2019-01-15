package com.example.monster.sampleproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private ActivityHelper ah = new ActivityHelper();
    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getConnectivityStatusString(context);

        if(status != "1" && status != "2" && status != "3") {
            ah.alertBox(context , "Uyarı" , "İnternet bağlantınızı aktif hale getiriniz");
        }
    }
}
