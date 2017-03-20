package com.devjiva.goconnect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import com.goconnect.events.http.Connection;
import com.goconnect.events.lib.Globals;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.util.SingletonUtil;
import com.lib.DialogBox;
import com.lib.Network;
import com.lib.bgtask.BackgroundTask;
import com.lib.bgtask.BackgroundThread;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends GoconnectActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    //Constants Data-members
    private static final String TAG = "SettingsActivity";

    //UI, cache and flag variable.
    private SharedPreference sharedPreference;
    private SeekBar distenceControl = null;
    private SwitchCompat swt_buzz, swt_share_safe;
    public static int progressChanged = 200;
    public static final int DISTENCE_LIMIT = 200;
    public static final int MY_PERMISSIONS_REQUEST_READ_LOCATION = 100;
    private boolean isModeChanged = false;
    private LocationManager mLocationManager;
    Activity context = this;
    private String fb_profileData;
    private String facebookId;
    private TextView textLogout;
    private SocialAuthAdapter logoutAdapter;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

//        initializeLocationManager();
        distenceControl = (SeekBar) findViewById(R.id.distence_bar);
        swt_buzz = (SwitchCompat) findViewById(R.id.switch_buzz);
        swt_buzz.setOnCheckedChangeListener(this);
        swt_share_safe = (SwitchCompat) findViewById(R.id.switch_share_safe);
        textLogout = (TextView) findViewById(R.id.textLogout);
        swt_share_safe.setOnCheckedChangeListener(this);
        textLogout.setOnClickListener(this);
        sharedPreference = new SharedPreference();

        if (sharedPreference.getIntValueWithKey(getApplicationContext(), "LocationDistenceInt") != 0) {
            progressChanged = sharedPreference.getIntValueWithKey(getApplicationContext(), "LocationDistenceInt");
        } else {
            sharedPreference.saveIntValueWithKey(getApplicationContext(), DISTENCE_LIMIT, "LocationDistenceInt");
        }

        swt_buzz.setChecked(!sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtBuzz"));
        swt_share_safe.setChecked(!sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe"));

        distenceControl.setProgress(progressChanged);

        fb_profileData = sharedPreference.getValueWithKey(context, "facebook_profileData");
        try {
            JSONObject obj = new JSONObject(fb_profileData);
            facebookId = obj.getJSONObject("facebook").getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        distenceControl.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= DISTENCE_LIMIT) {
                    seekBar.setProgress(DISTENCE_LIMIT);
                    progress = DISTENCE_LIMIT;
                }
                progressChanged = progress;
            }


            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progressChanged >= 10000) {
                    Toast.makeText(SettingsActivity.this, "Distence: 10km",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this,
                            "Distance: " + progressChanged + "m",
                            Toast.LENGTH_SHORT).show();
                }
                sharedPreference.saveIntValueWithKey(getApplicationContext(), progressChanged, "LocationDistenceInt");
            }

        });

//        if (!swt_share_safe.isChecked())
//            updateMyLocation(getApplicationContext(), true);
    }

    /* private void updateMyLocation(Context ctx, boolean value) {

         try {
             if (value) {
                 if (Build.VERSION.SDK_INT == 23) {
                     if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                         // TODO: Consider calling
                         ActivityCompat.requestPermissions(SettingsActivity.this,
                                 new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                 SettingsActivity.MY_PERMISSIONS_REQUEST_READ_LOCATION);

                         //    ActivityCompat#requestPermissions
                         // here to request the missing permissions, and then overriding
                         //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                         //                                          int[] grantResults)
                         // to handle the case where the user grants the permission. See the documentation
                         // for ActivityCompat#requestPermissions for more details.
                         return;
                     } else {
                         startBackService();
                     }
                 } else if (Build.VERSION.SDK_INT < 23) {
                     startBackService();
                 }

             } else {
                 ctx.stopService(new Intent(ctx, MyLocationUpdateService.class));
                 Log.d(TAG, "updateMyLocation: Service stopped.................");
             }

         } catch (Exception ex) {

         }
     }


     private void initializeLocationManager() {
         Log.e(TAG, "initializeLocationManager");
         if (mLocationManager == null) {
             mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
         }
     }

     private void startBackService() {
         getApplicationContext().startService(new Intent(getApplicationContext(), MyLocationUpdateService.class)
                 .putExtra("Dashboard", true));
         Log.d(TAG, "updateMyLocation: Service started.................");
     }
 */
    @SuppressLint("NewApi")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

            case R.id.switch_buzz:
                Log.d(TAG, "onCheckedChanged: buzz: " + isChecked);

                if (!isChecked) {
                    if (Build.VERSION.SDK_INT >= 22)
                        swt_buzz.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_green, null));
                    else if (Build.VERSION.SDK_INT >= 16)
                        swt_buzz.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_green));
                    else
                        swt_buzz.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_green));

                    Toast.makeText(SettingsActivity.this, " Buzz Switch is on!!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, " Buzz Switch is off!!",
                            Toast.LENGTH_SHORT).show();

                    if (Build.VERSION.SDK_INT >= 22)
                        swt_buzz.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_gray, null));
                    else if (Build.VERSION.SDK_INT >= 16)
                        swt_buzz.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_gray));
                    else
                        swt_buzz.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_gray));

                }
                Log.d(TAG, "onCheckedChanged: Buzzmode" + !isChecked);
                sharedPreference.saveBoolValueWithKey(getApplicationContext(), !isChecked, "SwtBuzz");
                break;

            case R.id.switch_share_safe:
                isModeChanged = true;
                Log.d(TAG, "onCheckedChanged: mode: " + isChecked);
//                if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (!isChecked) {
//				Toast.makeText(SettingsActivity.this, " Share Mode !!",
//						Toast.LENGTH_SHORT).show();
//                        updateMyLocation(getApplicationContext(), true);

                    if (Build.VERSION.SDK_INT >= 22)
                        swt_share_safe.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_green, null));
                    else if (Build.VERSION.SDK_INT >= 16)
                        swt_share_safe.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_green));
                    else
                        swt_share_safe.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_green));
                } else {
//				Toast.makeText(SettingsActivity.this, " Safe Mode !!",
//						Toast.LENGTH_SHORT).show();
//                    updateMyLocation(getApplicationContext(), false);

                    if (Build.VERSION.SDK_INT >= 22)
                        swt_share_safe.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_gray, null));
                    else if (Build.VERSION.SDK_INT >= 16)
                        swt_share_safe.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_gray));
                    else
                        swt_share_safe.setTrackDrawable(this.getResources().getDrawable(R.drawable.rectangle_bg_gray));
                }
                Log.d(TAG, "onCheckedChanged: sharemode" + !isChecked);

                sharedPreference.saveBoolValueWithKey(getApplicationContext(), !isChecked, "SwtShareSafe");
                updateModeToServer();
//                }
                break;

            default:
                break;
        }
    }

    private void updateModeToServer() {
//        JSONObject finalProfileObje = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            obj.put("facebookid", facebookId);
            String mode;
            if (sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe")) {
                mode = "on";
            } else {
                mode = "off";
            }
            obj.put("mode", mode);
//            finalProfileObje.putOpt("position", obj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        callToUpdateMode(obj);
    }

    private void callToUpdateMode(final JSONObject finalProfileObje) {


        new BackgroundTask(SettingsActivity.this, new BackgroundThread() {

            @Override
            public void taskCompleted(Object data) {
                try {
                    if (!Network.isAvailable(SettingsActivity.this)) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_NETWORK_NOT_FOUND);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else if (data instanceof Exception) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else {
                        try {
                            Log.d(TAG, data.toString());
                            JSONObject jsonObject = new JSONObject(data.toString());
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
//                                nearUserDataObject = data.toString();
//                                // TODO: 30-Jun-16 Implement Logout code
                                Toast.makeText(SettingsActivity.this, "Mode is updated", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                            newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                    newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                }
            }

            @Override
            public Object runTask() {
                try {
                    Object response;
                    try {
                        response = Connection.callHttpPostRequestsV2(Connection.URL_BASE + "api/users/updateAppMode", finalProfileObje);
                        Log.d(TAG, "runTask: " + Connection.URL_BASE + "api/users/updateAppMode" + finalProfileObje);
                        return response;
                    } catch (Exception e) {
                        return e;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                }
            }
        }, Globals.LOADING).execute();
    }

    /* @Override
     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults);

         switch (requestCode) {
             case MY_PERMISSIONS_REQUEST_READ_LOCATION: {
                 // If request is cancelled, the result arrays are empty.
                 if (grantResults.length > 0
                         && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     startBackService();
                 } else {
                     Toast.makeText(SettingsActivity.this, "Permission denied!! You won't get notifications ", Toast.LENGTH_SHORT).show();
                 }
                 return;
             }

             // other 'case' lines to check for other
             // permissions this app might request
         }
     }
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isModeChanged) {
            if (getIntent().getStringExtra("BundleFrom") != null) {
                if (getIntent().getStringExtra("BundleFrom").toString().equals("Dashboard")) {
                    Intent intent = new Intent(SettingsActivity.this, Dashboard.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
            Intent intent = new Intent(SettingsActivity.this, Dashboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else
            super.onBackPressed();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.textLogout:

                AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                alertDialog.setTitle("GOconnect");
                alertDialog.setMessage("Do you want to logout?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getStackActivities();
                                if (SingletonUtil.getSingletonConfigInstance().checkNetConnection(SettingsActivity.this))
                                    callLogoutWebservice();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();
                break;
        }
    }

    private void callLogoutWebservice() {
        new BackgroundTask(SettingsActivity.this, new BackgroundThread() {

            @Override
            public void taskCompleted(Object data) {
                try {
                    if (!Network.isAvailable(SettingsActivity.this)) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_NETWORK_NOT_FOUND);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else if (data instanceof Exception) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else {
                        try {
                            Log.d(TAG, data.toString());
                            JSONObject jsonObject = new JSONObject(data.toString());
                            if (jsonObject.getString("status").equalsIgnoreCase("success")) {
//                                nearUserDataObject = data.toString();
                                // TODO: 30-Jun-16 Implement Logout code

                                logoutAdapter = LoginActivity.adapter;
                                if (null == logoutAdapter) {
                                    logoutAdapter = new SocialAuthAdapter(new ResponseListener());
                                    logoutAdapter.addProvider(SocialAuthAdapter.Provider.FACEBOOK, R.drawable.facebook);
                                    logoutAdapter.addConfig(SocialAuthAdapter.Provider.FACEBOOK, "351657481663875", "c505baa9c08a42c2acc5eddaee98125c", null);
                                    logoutAdapter.authorize(SettingsActivity.this, SocialAuthAdapter.Provider.FACEBOOK);
                                }
                                boolean isloggedOut = logoutAdapter.signOut(getApplicationContext(), SocialAuthAdapter.Provider.FACEBOOK.toString());
                                Log.d(TAG, "taskCompleted: isloggedOut" + isloggedOut);
                                sharedPreference.clearSharedPreference(getApplicationContext());
                                SharedPreferences mLoginPrefs = getSharedPreferences(Globals.LOGIN_SPF_NAME, 0);
                                mLoginPrefs.edit().clear().commit();

                                Intent intent = new Intent(SettingsActivity.this, SplashActivity.class);
                                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK | intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                //                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                getStackActivities();
                                finish();
                            }

                        } catch (Exception e) {
                            DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                            newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                    newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                }
            }

            @Override
            public Object runTask() {
                try {
                    Object response;
                    try {
                        response = Connection.callHttpGetRequestsV2(Connection.URL_BASE + "api/users/userSignOut/" + facebookId);
                        Log.d(TAG, "runTask: " + Connection.URL_BASE + "api/users/userSignOut/" + facebookId);
                        return response;
                    } catch (Exception e) {
                        return e;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return e;
                }
            }
        }, Globals.LOADING).execute();
    }

    private void getStackActivities() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        int sizeStack = am.getRunningTasks(Integer.MAX_VALUE).size();

        for (int i = 0; i < sizeStack; i++) {
            Log.d(TAG, "Stack" + am.getRunningTasks(Integer.MAX_VALUE).get(i).toString());
        }
    }


    //Test Code
    //ResponseListener Class
    public final class ResponseListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {

            String providerName = values.getString(SocialAuthAdapter.PROVIDER);

            // Set a application wide reference to the social adapter here
            /*myApp = (MyApplication) getApplication();
            myApp.setSocialAuthAdapter(adapter);*/
        }

        @Override
        public void onError(SocialAuthError error) {
            Log.d("Custom-UI", "Error");
            error.printStackTrace();
        }

        @Override
        public void onCancel() {
            Log.d("Custom-UI", "Cancelled");
        }

        @Override
        public void onBack() {
            Log.d("Custom-UI", "Dialog Closed by pressing Back Key");

        }
    }

    // .Test Code
}