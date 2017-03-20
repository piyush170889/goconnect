package com.devjiva.goconnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;


import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.devjiva.goconnect.locations.MyLocationUpdateServiceNew;
import com.goconnect.events.http.Connection;

import com.goconnect.events.lib.Globals;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.util.SingletonUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.lib.DialogBox;
import com.lib.Network;
import com.lib.bgtask.BackgroundTask;
import com.lib.bgtask.BackgroundThread;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


import me.leolin.shortcutbadger.ShortcutBadger;


@SuppressLint("NewApi")
public class Dashboard extends SwipeActivity implements OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //    private GestureDetectorCompat gestureDetectorCompat;
    private static final String TAG = "DashboardActivity";
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_GPS = 100;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_API = 110;
    private static final int MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_SERVICE = 101;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final long INTERVAL_FOR_LOCATION = 1 * 60 * 1000;
    private static final long FASTEST_INTERVAL_FOR_LOCATION = 5 * 60 * 1000;

    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    LinearLayout layout100Meters, layout1km, layout10km, layoutFurther;
    com.goconnect.events.lib.CircularImageView cImageView_user_icon;
    LayoutInflater inflater;
    private String fb_profileData, nearUserDataObject;
    private String fb_profile_image, userProfileData;
    Activity context = this;
    private ImageLoader imageLoader;
    private SharedPreference sharedPreference;
    private String distanceRange = "100m";
    String facebookId;
    public DisplayImageOptions doptions;
    private ScrollView scrollView;
    private int getNearCallCount = 0;
    private LocationManager mLocationManager;
    private String lat;
    private String lng;
    private boolean booleanForService = false;
    private boolean isServiceRunning = false;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.splash, menu);
        MenuItem item = menu.findItem(R.id.action_share);
//        SubMenu submenu = item.addSubMenu(0, Menu.NONE, 1, "New Form").setIcon(R.drawable.settings);
//        submenu.add("Form 1").setIcon(R.drawable.settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.action_settings:
                // Toast.makeText(this, "Settings selected", Toast.LENGTH_SHORT).show();
                openSettingPage();
                break;

            case android.R.id.home:
                finish();
                break;

            case R.id.share_by_gmail:
                String to = "";
                String subject = "Try-out  GOconnect App";
                String message = "Please take this app for a spin with some people near you.(only Android 4 and up, sorry!)  Be aware that we will clear the database and you are only granted access for this short initial testing period\n" +
                        "\n\nDownload GoConnect app from the below link and install \n www.bit.ly/TestGOconnectAppAndroid" +
                        "\n\nAll feedback is welcome on \n www.bit.ly/GOconnectTestingfeedback ";

                final Intent intentToMail = new Intent(Intent.ACTION_SENDTO);
                intentToMail.setData(Uri.parse("mailto:"));
                // intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                intentToMail.putExtra(Intent.EXTRA_SUBJECT, subject);
                intentToMail.putExtra(Intent.EXTRA_TEXT, message);
                if (intentToMail.resolveActivity(getPackageManager()) != null)
                    startActivity(intentToMail);
                break;

            default:
                break;
        }

        return true;
    }


    private void openSettingPage() {
//        onPause();
        Intent intent = new Intent(Dashboard.this, SettingsActivity.class);
        intent.putExtra("BundleFrom", "Dashboard");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getSupportActionBar().hide();
//		getSupportActionBar().setBackgroundDrawable(new 
//				ColorDrawable(0xCCFF00));

        sharedPreference = new SharedPreference();
        initializeLocationManager();

        setContentView(R.layout.activity_dashboard_face_new);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL_FOR_LOCATION);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL_FOR_LOCATION);

//        SingletonUtil.getSingletonConfigInstance().isConnectedToInternet(this);
//        SingletonUtil.getSingletonConfigInstance().isConnectedToInternet(this);

        layout100Meters = (LinearLayout) findViewById(R.id.dashbord_hundred_meters_layout);
        layout1km = (LinearLayout) findViewById(R.id.dashbord_onekm_layout);
        layout10km = (LinearLayout) findViewById(R.id.dashbord_tenkm_layout);
        layoutFurther = (LinearLayout) findViewById(R.id.dashbord_further_layout);
        cImageView_user_icon = (com.goconnect.events.lib.CircularImageView) findViewById(R.id.user_icon);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setEnabled(false);

       /* //service to get notification for nearby user
        startService(new Intent(Dashboard.this, NearUserLocationUpdateService.class)
                .putExtra("Dashboard", true));*/

        /*IMAGELOADER TO IMAGEVIEW*/
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        doptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.defaultuser).showImageOnFail(R.drawable.defaultuser).showImageOnLoading(R.drawable.defaultuser)
                .cacheInMemory(true).build();
//		imageLoader = new ImageLoader();
        try {
            userProfileData = sharedPreference.getValueWithKey(context, "UserProfileData");
            Log.d(TAG, "onCreate: UserProfileData*********************" + userProfileData);
            fb_profileData = sharedPreference.getValueWithKey(context, "facebook_profileData");
            fb_profile_image = sharedPreference.getValueWithKey(context, "fb_profile_image");
            Log.d(TAG, "onCreate: fb_profile_image " + fb_profile_image);

            JSONObject obj = new JSONObject(fb_profileData);
            facebookId = obj.getJSONObject("facebook").getString("id");

           /* Picasso.with(this).load(fb_profile_image)
                    .error(R.drawable.defaultuser)
                    .into(cImageView_user_icon, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "onSuccess: Picasso");
                        }

                        @Override
                        public void onError() {
                            Picasso.with(Dashboard.this).load(R.drawable.defaultuser).into(cImageView_user_icon);
                        }
                    });
*/

            // DisplayImage function from ImageLoader Class

//            imageLoader.DisplayImage(fb_profile_image, cImageView_user_icon);
            imageLoader.displayImage(fb_profile_image, cImageView_user_icon, doptions);

        } catch (Exception ex) {
        }

        inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        findViewById(R.id.user_icon).setOnClickListener(this);
        findViewById(R.id.txt_face_institution).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				showMoreUsers(distanceRange);
                Intent intent = new Intent(Dashboard.this, DashboardInstitution.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("nearUserDataObject", nearUserDataObject);
                startActivity(intent);
            }
        });

        findViewById(R.id.txt_face_topic).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, DashboardTopic.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtra("nearUserDataObject", nearUserDataObject);
                startActivity(intent);
            }
        });


//        gestureDetectorCompat = new GestureDetectorCompat(this, new MyGestureListener());
       /* if (getNearCallCount == 0)
            getNearUser();*/
    }

  /*  private boolean servicesAvailable() {

        int resultCode = GoogleApiAvailability.isGooglePlayServicesAvailable(Dashboard.this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            GoogleApiAvailability.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: DASHBOARD");
        //if user opens dashboard after notification, then set user to share mode so that user can view another user details
        Log.d(TAG, "onCreate: " + sharedPreference.getBoolValueWithKey(getApplicationContext(), "isFromNotification"));

        if (isMyServiceRunning(MyLocationUpdateServiceNew.class)) {
            isServiceRunning = true;
        } else {
            isServiceRunning = false;
            callBackService();
        }

        if (sharedPreference.getBoolValueWithKey(getApplicationContext(), "isFromNotification")) {
            sharedPreference.saveBoolValueWithKey(getApplicationContext(), false, "isFromNotification");
            boolean success = ShortcutBadger.removeCount(Dashboard.this);
//            Toast.makeText(getApplicationContext(), "success=" + success, Toast.LENGTH_SHORT).show();
        }

        if (SingletonUtil.getSingletonConfigInstance().checkNetConnection(this)) {
            if (getNearCallCount == 0)
                //webservice called for first time (oncreate)
                getNearUser();

            else if (getNearCallCount > 0) {
                //webserevice called for onresume
               /* if (((LinearLayout) layout1km).getChildCount() > 0) {
                    ((LinearLayout) layout1km).removeAllViewsInLayout();
                }
                if (((LinearLayout) layout10km).getChildCount() > 0) {
                    ((LinearLayout) layout10km).removeAllViewsInLayout();
                }
                if (((LinearLayout) layout100Meters).getChildCount() > 0) {
                    ((LinearLayout) layout100Meters).removeAllViewsInLayout();
                }
                if (((LinearLayout) layoutFurther).getChildCount() > 0) {
                    ((LinearLayout) layoutFurther).removeAllViewsInLayout();
                }*/
                getNearUser();
            }
        } else {
            if (((LinearLayout) layout1km).getChildCount() > 0) {
                ((LinearLayout) layout1km).removeAllViewsInLayout();
            }
            if (((LinearLayout) layout10km).getChildCount() > 0) {
                ((LinearLayout) layout10km).removeAllViewsInLayout();
            }
            if (((LinearLayout) layout100Meters).getChildCount() > 0) {
                ((LinearLayout) layout100Meters).removeAllViewsInLayout();
            }
            if (((LinearLayout) layoutFurther).getChildCount() > 0) {
                ((LinearLayout) layoutFurther).removeAllViewsInLayout();
            }

            SingletonUtil.getSingletonConfigInstance().showErrorDialog(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: DASHBOARD");
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: DASHBOARD");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: DASHBOARD");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        callBackService();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
//            LocationServices.FusedLocationApi.removeLocationUpdates()

        }
        Log.d(TAG, "onDestroy: DASHBOARD");
    }

    public void getNearUser() {

        lat = sharedPreference.getValueWithKey(getApplicationContext(), "Latitude");
        lng = sharedPreference.getValueWithKey(getApplicationContext(), "Longitude");

        if (lat == null || lng == null) {
//                afflicationObj.put(0, "79.2671809");
//                afflicationObj.put(1, "17.052389");
//                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    askPermissionGPS();
//                } else {
            checkGps();
        }
//                afflicationObj.put(0, lng);
//                afflicationObj.put(1, lat);
//                }
        else {
            requestToNearUser();
        }


    }

    private void requestToNearUser() {
        JSONObject finalProfileObje = new JSONObject();
        JSONArray afflicationObj = new JSONArray();

        try {
            afflicationObj.put(0, lng);
            afflicationObj.put(1, lat);
            finalProfileObje.put("pageNum", "0");
            finalProfileObje.put("currLocation", afflicationObj);
            finalProfileObje.put("facebookId", facebookId);
           /* if (getNearCallCount > 0) {
                //webserevice called for onresume
                if (((LinearLayout) layout1km).getChildCount() > 0) {
                    ((LinearLayout) layout1km).removeAllViewsInLayout();
                }
                if (((LinearLayout) layout10km).getChildCount() > 0) {
                    ((LinearLayout) layout10km).removeAllViewsInLayout();
                }
                if (((LinearLayout) layout100Meters).getChildCount() > 0) {
                    ((LinearLayout) layout100Meters).removeAllViewsInLayout();
                }
                if (((LinearLayout) layoutFurther).getChildCount() > 0) {
                    ((LinearLayout) layoutFurther).removeAllViewsInLayout();
                }
            }*/
            getNearUserFromServer(finalProfileObje);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_SERVICE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //start back service
                    if (!isServiceRunning)
                        startBackService();
                    Log.d(TAG, "onRequestPermissionsResult: Service started");

                } else {
                    Toast.makeText(Dashboard.this, "Permission denied!! You won't get notifications ", Toast.LENGTH_SHORT).show();
                }
                break;


            case MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_API:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        if (googleApiClient != null) {
                            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
                        }

                    } catch (SecurityException se) {
                        se.printStackTrace();
                    }
                } else {
                    Toast.makeText(Dashboard.this, "Permission denied!! You won't get notifications ", Toast.LENGTH_SHORT).show();
                }
                break;

            case MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_GPS:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    coonectGoogleApiClient();
                } else {
                    Toast.makeText(Dashboard.this, "Permission denied!! You won't get notifications ", Toast.LENGTH_SHORT).show();
                }
                break;

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    private void checkGps() {
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final android.app.AlertDialog.Builder adb2 = new android.app.AlertDialog.Builder(
                    Dashboard.this);

            adb2.setMessage("Please turn ON GPS");
            adb2.setPositiveButton("SETTING", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            adb2.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });
            adb2.show();
        } else
            onGpsIsOn();
    }


    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
    }

    private void onGpsIsOn() {


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(Dashboard.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_GPS);

                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                coonectGoogleApiClient();
            }
        } else {
            coonectGoogleApiClient();
        }
    }

    private void coonectGoogleApiClient() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    private void setLatLongInSharedPref(Location location) {
        try {
            lat = "" + location.getLatitude();
            lng = "" + location.getLongitude();

            sharedPreference.saveWithKey(getApplicationContext(), "" + location.getLatitude(), "Latitude");
            sharedPreference.saveWithKey(getApplicationContext(), "" + location.getLongitude(), "Longitude");
            requestToNearUser();

        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Toast.makeText(Dashboard.this, "GPS is connecting ..Please try after some time", Toast.LENGTH_SHORT).show();
        }
    }


    public void getNearUserFromServer(final JSONObject finalProfileObje) {

        new BackgroundTask(Dashboard.this, new BackgroundThread() {

            @Override
            public void taskCompleted(Object data) {
                try {
                    if (!Network.isAvailable(Dashboard.this)) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_NETWORK_NOT_FOUND);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else if (data instanceof Exception) {
                        DialogBox newFragment = DialogBox.newInstance(Globals.ALERT_UNEXPECTED_ERROR);
                        newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                    } else {
                        try {
                            Log.d(TAG, data.toString());
                            if (data.toString().contains("errMsg")) {
                                JSONObject jsonObject = new JSONObject(data.toString());
                                String errorMessage = jsonObject.getString("errMsg");
                                DialogBox newFragment = DialogBox.newInstance(errorMessage);
                                newFragment.show(getSupportFragmentManager(), DialogBox.TAG_DIALOG);
                            } else {
                                JSONObject jsonObject = new JSONObject(data.toString());
                                if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                                    nearUserDataObject = data.toString();
                                    JSONObject jsonUsers = new JSONObject(jsonObject.getString("users"));
                                    JSONArray JsonArrayDlist1 = new JSONArray(jsonUsers.getString("distl1"));
                                    JSONArray JsonArrayDlist2 = new JSONArray(jsonUsers.getString("distl2"));
                                    JSONArray JsonArrayDlist3 = new JSONArray(jsonUsers.getString("distl3"));
                                    JSONArray JsonArrayMoreData = new JSONArray(jsonUsers.getString("moreData"));
                                    Log.d(TAG, "************* SwtShareSafe*****" + sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe"));
                                    if (!sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe"))
                                        Toast.makeText(Dashboard.this, "Plese turn on the sharemode to view users", Toast.LENGTH_SHORT).show();

                                    UpdateDlist1(JsonArrayDlist1);
                                    UpdateDlist2(JsonArrayDlist2);
                                    UpdateDlist3(JsonArrayDlist3);
                                    UpdateMoreData(JsonArrayMoreData);

                                    getNearCallCount++;
                                    if (googleApiClient != null) {
                                        if (googleApiClient.isConnected() || googleApiClient.isConnecting()) {
                                            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, Dashboard.this);
                                            googleApiClient.disconnect();
                                        }
                                    }
                                }
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
                        response = Connection.callHttpPostRequestsV2(Connection.URL_BASE + "api/users/newNearUsers", finalProfileObje);
                        Log.d(TAG, "runTask: " + Connection.URL_BASE + "api/users/newNearUsers" + finalProfileObje.toString());
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

    private void UpdateMoreData(JSONArray jsonArray) {

        if (((LinearLayout) layoutFurther).getChildCount() > 0) {
            ((LinearLayout) layoutFurther).removeAllViewsInLayout();
        }
        distanceRange = "Further";
        UpdateUI(jsonArray, layoutFurther);
    }

    private void UpdateDlist3(JSONArray jsonArray) {


        if (((LinearLayout) layout10km).getChildCount() > 0) {
            ((LinearLayout) layout10km).removeAllViewsInLayout();
        }
        distanceRange = "10km";
        UpdateUI(jsonArray, layout10km);
    }

    private void UpdateDlist2(JSONArray jsonArray) {
        if (((LinearLayout) layout1km).getChildCount() > 0) {
            ((LinearLayout) layout1km).removeAllViewsInLayout();
        }
        distanceRange = "1km";
        UpdateUI(jsonArray, layout1km);
    }

    private void UpdateDlist1(JSONArray jsonArray) {
        if (((LinearLayout) layout100Meters).getChildCount() > 0) {
            ((LinearLayout) layout100Meters).removeAllViewsInLayout();
        }
        distanceRange = "200m";
        UpdateUI(jsonArray, layout100Meters);
    }

    public void UpdateUI(final JSONArray jsonArray, LinearLayout layout) {

        if (!sharedPreference.getBoolValueWithKey(getApplicationContext(), "SwtShareSafe"))
            layout.setVisibility(View.GONE);
        else
            layout.setVisibility(View.VISIBLE);

        int rowLimit = 3;
        int dataSize = jsonArray.length();
        int dataSizeTemp = 0;
        int dataSizeLimit = dataSize;
        float weight = 2f;
        LinearLayout ll = null;
        if (jsonArray.length() < rowLimit) {
            rowLimit = jsonArray.length();
        }
        for (int i = 0; i < dataSize; i++) {
            if (dataSizeTemp == 0) {
                dataSizeLimit = dataSizeLimit - 3;
                ll = null;
                ll = new LinearLayout(getApplicationContext());
                ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                ll.setPadding(2, 5, 2, 5);
                ll.setOrientation(LinearLayout.HORIZONTAL);
            }
            if (dataSizeTemp < rowLimit) {
                try {
                    final JSONObject otherUserJSONObject = (JSONObject) jsonArray.get(i);
                    String url = otherUserJSONObject.getJSONObject("facebook").getString("facebookProfileImage");
                    Log.d(TAG, "UpdateUI: urlOfImage: " + url);
//                    imageLoader = new ImageLoader(getApplicationContext());
//                    imageLoader.clearCache();
                    imageLoader = ImageLoader.getInstance();
                    imageLoader.clearMemoryCache();
                    imageLoader.clearDiscCache();
                    imageLoader.clearDiskCache();

                    final com.goconnect.events.lib.CircularImageView cImageView1meater = (com.goconnect.events.lib.CircularImageView) inflater.inflate(R.layout.image_view_sample, null);
                    imageLoader.displayImage(url, cImageView1meater, doptions);

                    /*if (null != url || !url.isEmpty()) {

                        *//*Picasso picasso = new Picasso.Builder(this)
                                .listener(new Picasso.Listener() {
                                    @Override
                                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                                        //Here your log
                                        Picasso.with(Dashboard.this).load(R.drawable.defaultuser).into(cImageView1meater);
                                    }
                                }).build();
                        picasso.load(fb_profile_image).into(cImageView1meater);*//*
                        Picasso.with(Dashboard.this).load(fb_profile_image).into(cImageView1meater);
                    } else
                        Picasso.with(Dashboard.this).load(R.drawable.defaultuser).into(cImageView1meater);*/
//                    imageLoader.DisplayImage(url, cImageView1meater);

                    //if(rowLimit==3){weight=5f;}
                    int mainsize = getMobileSizeWidth() / 4;
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mainsize, mainsize);
                    layoutParams.setMargins(20, 20, 20, 0);
                    cImageView1meater.setLayoutParams(layoutParams);

                    cImageView1meater.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            showBusinessCardOfOtherUser(otherUserJSONObject);
                        }
                    });
                    if (ll != null) {
                        dataSizeTemp++;
                        ll.addView(cImageView1meater);
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            if (dataSizeTemp == rowLimit) {
                if (dataSizeLimit < dataSizeTemp)
                    rowLimit = dataSizeLimit;
                dataSizeTemp = 0;
                if (i == 9) {// to check more users this line need to comment .
//            		if(i==dataSize-1){//and this need to un comment .
                    LayoutParams lparams = new LayoutParams(
                            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(lparams);
                    tv.setPadding(20, 100, 0, 0);
                    tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    tv.setGravity(Gravity.LEFT);
                    tv.setTextColor(ContextCompat.getColor(this, R.color.white));
                    tv.setText("MoreData");
                    tv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showMoreUsers(distanceRange);
                        }
                    });

                    ll.addView(tv);
                    layout.addView(ll);
                    break;
                } else {
                    layout.addView(ll);
                }
                ll = null;
            }
        }
    }

    protected void showMoreUsers(String distanceRange) {
        Intent intent = new Intent(Dashboard.this, MoreUsersActivity.class);
        intent.putExtra("distanceRange", distanceRange);
        startActivity(intent);
    }

    public int getMobileSizeWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
        }

        int width = size.x;
        int height = size.y;
        return width;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_icon:
                showBusinessCardOfCurrentUser();
                break;

            default:
                break;
        }

    }


    private void callBackService() {
        if (Build.VERSION.SDK_INT == 23) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(Dashboard.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_SERVICE);

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
    }

    private void startBackService() {
        getApplicationContext().startService(new Intent(getBaseContext(), MyLocationUpdateServiceNew.class)
                .putExtra("Dashboard", true));
        Log.d(TAG, "updateMyLocation: Service started.................");
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void showBusinessCardOfOtherUser(JSONObject otherUserJSONObject) {
        onStop();
        Intent intent = new Intent(Dashboard.this, BusinessCardOtherUserActivity.class);
        Bundle b = new Bundle();
        b.putString("BusinessUserData", otherUserJSONObject.toString());
        intent.putExtras(b);
        startActivity(intent);
    }

    private void showBusinessCardOfCurrentUser() {
        onStop();
        Intent intent = new Intent(Dashboard.this, BusinessCardUserActivity.class);
        startActivity(intent);
    }

   /* @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }*/


    @Override
    protected void previous() {
        Intent intent = new Intent(Dashboard.this, DashboardTopic.class);
        intent.putExtra("nearUserDataObject", nearUserDataObject);
        startActivity(intent);
    }

    @Override
    protected void next() {
        Intent intent = new Intent(Dashboard.this, DashboardInstitution.class);
        intent.putExtra("nearUserDataObject", nearUserDataObject);
        startActivity(intent);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                ActivityCompat.requestPermissions(Dashboard.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_LOCATION_FOR_API);

                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                if (googleApiClient != null) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
                }
            }
        } else if (Build.VERSION.SDK_INT < 23) {
            if (googleApiClient != null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: " + location);
        mLastLocation = location;
        if (mLastLocation != null) {
            setLatLongInSharedPref(mLastLocation);
        }

        Log.d(TAG, "onLocationChanged: Lat=" + mLastLocation.getLatitude() + "Long=" + mLastLocation.getLatitude());
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                if (googleApiClient != null) {
                    googleApiClient.connect();
                }

            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }


    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((Dashboard) getActivity()).onDialogDismissed();
        }
    }
}