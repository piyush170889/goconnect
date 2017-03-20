package com.devjiva.goconnect.locations;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.goconnect.events.http.UpdateLocationIntentService;
import com.goconnect.events.lib.SharedPreference;
import com.goconnect.util.SingletonUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by MR JOSHI on 11-Jul-16.
 */
public class MyLocationUpdateServiceNew extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String TAG = "MyLocationUpdateService";
//    private LocationManager mLocationManager = null;

    private static final long LOCATION_FASTEST_INTERVAL = 15 * 60 * 1000;
    private static final long LOCATION_INTERVAL = 10 * 60 * 1000;
    private static float LOCATION_DISTANCE = 200;
    private SharedPreference sharedPreference;
    private final int locationUpdateDistance = 0;
    private final int locationUpdateTime = 1 * 60 * 1000;
    private boolean isFromProfile = false;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            isFromProfile = intent.getBooleanExtra("isFromProfile", false);

        super.onStartCommand(null, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

//        initializeLocationManager();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setFastestInterval(LOCATION_FASTEST_INTERVAL);

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }

        sharedPreference = new SharedPreference();
//        locationUpdateHandler = new Handler();
        try {
            if (sharedPreference.getIntValueWithKey(getApplicationContext(), "LocationDistenceInt") != 0) {
                LOCATION_DISTANCE = (float) sharedPreference.getIntValueWithKey(getApplicationContext(), "LocationDistenceInt");
            } else {
                sharedPreference.saveIntValueWithKey(getApplicationContext(), (int) LOCATION_DISTANCE, "LocationDistenceInt");
            }
//            locationUpdateHandler.postDelayed(locationUpdateRunnable, 500);
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
//        try {
//            mLocationManager.requestLocationUpdates(
//                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
//                    mLocationListeners[0]);
//        } catch (java.lang.SecurityException ex) {
//            Log.i(TAG, "fail to request location update, ignore", ex);
//        } catch (IllegalArgumentException ex) {
//            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
//        }
    }


    @Override
    public void onDestroy() {

//        locationUpdateHandler = null;
        super.onDestroy();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
//            LocationServices.FusedLocationApi.removeLocationUpdates()

        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {

//        Toast.makeText(getApplicationContext(), "onTaskRemoved called", Toast.LENGTH_LONG).show();

        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.setPackage(getPackageName());

        PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1,
                restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent);

        Log.d(TAG, "onTaskRemoved: Service restarted....");
        super.onTaskRemoved(rootIntent);
    }

    public void validateLatLogs(Location mlocation) {
        String Latitude, Longitude, fb_profileData;
        double dist;
        try {
//            Latitude = sharedPreference.getValueWithKey(getApplicationContext(), "Latitude");
//            Longitude = sharedPreference.getValueWithKey(getApplicationContext(), "Longitude");
            fb_profileData = sharedPreference.getValueWithKey(getApplicationContext(), "facebook_profileData");
//            if (Latitude != null && Longitude != null) {
            if ((mlocation.getLatitude() != 0) && (mlocation.getLongitude() != 0)) {
                // dist = calculateDistance(Double.parseDouble(Latitude),mlocation.getLatitude(), Double.parseDouble(Longitude),mlocation.getLongitude());
                //if(dist>1)// Distance greater than 1 kilometer update to server.
                {
                    sharedPreference.saveWithKey(getApplicationContext(), "" + mlocation.getLatitude(), "Latitude");
                    sharedPreference.saveWithKey(getApplicationContext(), "" + mlocation.getLongitude(), "Longitude");

                    Intent msgIntent = new Intent(getApplicationContext(), UpdateLocationIntentService.class);

                    msgIntent.putExtra("Latitude", "" + mlocation.getLatitude());
                    msgIntent.putExtra("Longitude", "" + mlocation.getLongitude());
                    msgIntent.putExtra("facebook_profileData", fb_profileData);
                    if (!isFromProfile) {
                        startService(msgIntent);
                    }
                }
            }
           /* } else {
                if ((mlocation.getLatitude() != 0) && (mlocation.getLongitude() != 0)) {
                    sharedPreference.saveWithKey(getApplicationContext(), "" + mlocation.getLatitude(), "Latitude");
                    sharedPreference.saveWithKey(getApplicationContext(), "" + mlocation.getLongitude(), "Longitude");
//                    sharedPreference.saveWithKey(getApplicationContext(), "18.51714121", "Latitude");
//                    sharedPreference.saveWithKey(getApplicationContext(), "73.84225200", "Longitude");
                    Intent msgIntent = new Intent(getApplicationContext(), UpdateLocationIntentService.class);
//                    msgIntent.putExtra("Latitude", "" + "18.51714121");
//                    msgIntent.putExtra("Longitude", "" + "73.84225200");
                    msgIntent.putExtra("Latitude", "" + mlocation.getLatitude());
                    msgIntent.putExtra("Longitude", "" + mlocation.getLongitude());
                    msgIntent.putExtra("facebook_profileData", fb_profileData);
                    if (!isFromProfile) {
                        startService(msgIntent);
                    }
                }
            }*/

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "onConnectionSuspended: ");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: " + location);
        mLastLocation = location;
        float accuracy = mLastLocation.getAccuracy();
        Log.d(TAG, "onLocationChanged: accuracy " + accuracy);
        if (mLastLocation != null) {
            validateLatLogs(mLastLocation);
        }

        Log.d(TAG, "onLocationChanged: Lat=" + mLastLocation.getLatitude() + "Long=" + mLastLocation.getLatitude());

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ");
    }


}