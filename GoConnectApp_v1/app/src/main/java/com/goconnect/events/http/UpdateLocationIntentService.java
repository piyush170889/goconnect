package com.goconnect.events.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.devjiva.goconnect.Dashboard;
import com.devjiva.goconnect.R;
import com.goconnect.events.lib.SharedPreference;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

public class UpdateLocationIntentService extends IntentService {


    private static String TAG = "MyLocationUpdateService";
    private ResponseStatusListener responseStatusListener;


    public UpdateLocationIntentService(String name) {
        super("UpdateLocationIntentService");
    }

    public UpdateLocationIntentService() {
        super("UpdateLocationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String requestStringLatitude = intent.getStringExtra("Latitude");
            String fb_profileData = intent.getStringExtra("facebook_profileData");
            String requestStringLongitude = intent.getStringExtra("Longitude");
            String facebookid = "";
            if (fb_profileData != null) {
                try {
                    JSONObject obj = new JSONObject(fb_profileData);
                    JSONObject obj1 = new JSONObject(obj.getString("facebook"));
                    facebookid = obj1.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject currLocationAndFbIdObje = new JSONObject();

                JSONObject currLocationObj = new JSONObject();

                currLocationObj.put("latitude", requestStringLatitude);

                currLocationObj.put("longitude", requestStringLongitude);

                SharedPreference sharedPreference = new SharedPreference();

                currLocationAndFbIdObje.put("facebookid", facebookid);
                currLocationAndFbIdObje.put("currLocation", currLocationObj);
                currLocationAndFbIdObje.put("distance", sharedPreference.getIntValueWithKey(getApplicationContext(), "LocationDistenceInt"));

                Log.d(TAG, "UpdateLocationIntentService:onHandleIntent: " + currLocationAndFbIdObje);

                Object response = Connection.callHttpPostRequestsV2(Connection.URL_BASE + "api/users/updateCurrLoc", currLocationAndFbIdObje);
//			Object response =Connection.callHttpGetRequestsV2(Connection.URL_BASE + "api/users/updateCurrLoc");

                Log.d(TAG, "UpdateLocationIntentService: onHandleIntent: " + response.toString());
                if (response.toString().contains("success")) {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String statusMessage = jsonObject.getString("status");

                    if (jsonObject.getInt("nearUsersCount") > 0)

                        checkAppIsOpen(jsonObject.getInt("nearUsersCount"));


                    if (null != responseStatusListener) {
                        responseStatusListener.onResponseSuccess();
                    }
                    Log.e("updateCurrLoc", "Request location update " + statusMessage);
                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("updateCurrLoc", "JSONException " + e.toString());
        }
    }

    private void checkAppIsOpen(final int nearUsersCount) {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServiceInfo = null;
        List<ActivityManager.RunningTaskInfo> runningTaskInfo = null;

        boolean isActivityFound = false;

        if (android.os.Build.VERSION.SDK_INT > 21) {
            ActivityManager mgr = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> tasks = mgr.getAppTasks();
            String packagename;
            String label = null;
            for (ActivityManager.AppTask task : tasks) {
                packagename = task.getTaskInfo().baseIntent.getComponent().getPackageName();
                try {
                    label = getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packagename, PackageManager.GET_META_DATA)).toString();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                Log.v(TAG, packagename + ":" + label);
                if (label.equalsIgnoreCase(getPackageName().toString())) {
                    isActivityFound = true;
                }
            }


        } else {

            runningTaskInfo = activityManager.getRunningTasks(Integer.MAX_VALUE);
            if (runningTaskInfo.get(0).topActivity.getPackageName().toString()
                    .equalsIgnoreCase(getPackageName().toString())) {
                isActivityFound = true;
            }

        }


        if (isActivityFound) {
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UpdateLocationIntentService.this, nearUsersCount + " New User Found..Please Refresh screen", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // write your code to build a notification.
            // return the notification you built here
            sendNotification(nearUsersCount);
        }

    }

    public interface ResponseStatusListener {
        void onResponseSuccess();
    }

    private void sendNotification(final int nearUsersCount) {
        try {
            Handler mainHandler = new Handler(Looper.getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    setBadge(getBaseContext(), nearUsersCount);
                }
            });


/*            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setBadge(getBaseContext(), nearUsersCount);
                }
            }, 10);*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        String message = nearUsersCount + " Near UWcers detected";
        Log.d(TAG, "sendNotification: " + nearUsersCount);
        Intent intent = new Intent(this, Dashboard.class);
        intent.putExtra("Message", message);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.gologoapp)
                .setContentTitle("GoConnect")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        //vibrate on receiving notification
        SharedPreference sharedPreference = new SharedPreference();

        Log.d(TAG, "UpdateLocationIntentService: sendNotification: " + sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtBuzz"));

        if (sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtBuzz"))
            notificationBuilder.setVibrate(new long[]{1000, 1000});

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        sharedPreference.saveBoolValueWithKey(getApplicationContext(), true, "isFromNotification");
    }

    public static void setBadge(Context context, int count) {
        boolean success = ShortcutBadger.applyCount(context, count);
//        SharedPreference sharedPreference = new SharedPreference();
//        sharedPreference.saveBoolValueWithKey(context, true, "isBadgeSet");
//        Toast.makeText(context, "Set count=" + count + ", success=" + success, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "setBadge: " + count);
    }

    public static String getLauncherClassName(Context context) {

        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

}
