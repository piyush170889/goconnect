package com.goconnect.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.devjiva.goconnect.R;


/**
 * Created by MR JOSHI on 14-Mar-16.
 */
public class SingletonUtil {
    private static SingletonUtil singletonUtil;
    private String TAG = "SingletonUtil";

    private SingletonUtil() {
    }

    public static SingletonUtil getSingletonConfigInstance() {
        if (singletonUtil == null)
            singletonUtil = new SingletonUtil();
        return singletonUtil;
    }

    public void isConnectedToInternet(final Context mContext) {

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected() == true) {
         Log.d(TAG,"Connected To internet");
        }
        else{
          showErrorDialog(mContext);
        }
    }

    public void showErrorDialog(final Context mContext) {
        android.app.AlertDialog.Builder removeItemDialog = new android.app.AlertDialog.Builder(mContext);
        removeItemDialog.setMessage("Turn ON Wifi or Mobile Data");
        removeItemDialog.setPositiveButton("Turn ON", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mContext.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
            }
        });
        removeItemDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        removeItemDialog.show();
    }

    public boolean checkNetConnection(final Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        boolean wifiConnectionStatus = false;
        boolean mobileConnectionStatus = false;
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                wifiConnectionStatus = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                mobileConnectionStatus = true;
            }
        } else {
            // not connected to the internet
//            Toast.makeText(context, context.getString(R.string.check_net_connection), Toast.LENGTH_SHORT).show();
        }

        if (mobileConnectionStatus == true || wifiConnectionStatus == true) return true;

        else return false;
    }


}
