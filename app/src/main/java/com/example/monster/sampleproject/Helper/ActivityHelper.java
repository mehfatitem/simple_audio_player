package com.example.monster.sampleproject.Helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.io.InputStream;


public class ActivityHelper {

    public boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be null
        return (netInfo != null && netInfo.isConnected());
    }

    public void alertBox(final Context context, String title, String messageContent) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(messageContent);


        // Setting Positive "Yes" Btn
        alertDialog.setPositiveButton("TAMAM",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        /*Toast.makeText(context,
                                "You clicked on YES", Toast.LENGTH_SHORT)
                                .show();*/
                        dialog.dismiss();
                    }
                });

        // Setting Negative "NO" Btn
        /*alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog
                        Toast.makeText(context,
                                "You clicked on NO", Toast.LENGTH_SHORT)
                                .show();
                        dialog.cancel();
                    }
                });*/
        alertDialog.show();
        alertDialog.setCancelable(false);
    }

    public String loadJSONFromAsset(final Context context , String jsonFileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}
