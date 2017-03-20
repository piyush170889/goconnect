package com.goconnect.events.http;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.devjiva.goconnect.Dashboard;
import com.devjiva.goconnect.R;
import com.goconnect.events.lib.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MR JOSHI on 27-Jun-16.
 */
public class NearUserUpdateLocationIntentService extends IntentService {

    private String TAG = "UpdateLocationIntentService";
    private ResponseStatusListener responseStatusListener;


    public NearUserUpdateLocationIntentService(String name) {
        super("UpdateLocationIntentService");
    }

    public NearUserUpdateLocationIntentService() {
        super("UpdateLocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String requestStringLatitude = intent.getStringExtra("Latitude");
            String requestStringLongitude = intent.getStringExtra("Longitude");
            String fb_profileData = intent.getStringExtra("facebook_profileData");
            String facebookid = "";
            try {
                JSONObject obj = new JSONObject(fb_profileData);
                JSONObject obj1 = new JSONObject(obj.getString("facebook"));

                facebookid = obj1.getString("id");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JSONObject finalProfileObje = new JSONObject();

            JSONObject currLocationObj = new JSONObject();

            currLocationObj.put("latitude", requestStringLatitude);

            currLocationObj.put("longitude", requestStringLongitude);

            finalProfileObje.put("pageNum", "0");
            finalProfileObje.put("currLocation", currLocationObj);
            finalProfileObje.put("facebookId", facebookid);

            Log.d(TAG, "onHandleIntent: " + finalProfileObje);

            Object response = Connection.callHttpPostRequestsV2(Connection.URL_BASE + "api/users/newNearUsers", finalProfileObje);
//			Object response =Connection.callHttpGetRequestsV2(Connection.URL_BASE + "api/users/updateCurrLoc");

            Log.d(TAG, "onHandleIntent: " + response.toString());
            if (response.toString().contains("success")) {
                JSONObject jsonObject = new JSONObject(response.toString());
                String statusMessage = jsonObject.getString("status");

                sendNotification();
//                if (null != responseStatusListener) {
//                    responseStatusListener.onResponseSuccess();
//                }
                Log.e("updateCurrLoc", "Request location update " + statusMessage);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("updateCurrLoc", "JSONException " + e.toString());
        }
    }


    public interface ResponseStatusListener {

        void onResponseSuccess();

    }

    private void sendNotification() {
        String message = "Near User detected";
        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("Message", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("GoConnect")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);


        //vibrate on receiving notification
        SharedPreference sharedPreference = new SharedPreference();

        Log.d(TAG, "sendNotification: buzzMode" + sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtBuzz"));
        if (sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtBuzz"))
            notificationBuilder.setVibrate(new long[]{1000, 1000});


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
