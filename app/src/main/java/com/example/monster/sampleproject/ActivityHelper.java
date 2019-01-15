package com.example.monster.sampleproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


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

}
