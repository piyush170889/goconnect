package com.devjiva.goconnect.locations;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.goconnect.events.http.NearUserUpdateLocationIntentService;
import com.goconnect.events.http.UpdateLocationIntentService;
import com.goconnect.events.lib.SharedPreference;

import org.json.JSONObject;

/**
 * Created by MR JOSHI on 27-Jun-16.
 */
public class NearUserLocationUpdateService extends Service implements UpdateLocationIntentService.ResponseStatusListener {
    private static final String TAG = "MyLocationUpdateService";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static float LOCATION_DISTANCE = 0;
    private SharedPreference sharedPreference;
    private Handler locationUpdateHandler;
    private boolean isFromProfile = false;


    private Runnable locationUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (null != location)
                validateLatLogs(location);
                else
                    validateLatLogs(mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }


            // TODO: 25-Jun-16 Comment below line after implementing interface issue
            locationUpdateHandler.postDelayed(this, 300000);
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null)
            isFromProfile = intent.getBooleanExtra("isFromProfile", false);
        Log.e(TAG, "onStartCommand NearUserLocationUpdateService started......................");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        sharedPreference = new SharedPreference();
        locationUpdateHandler = new Handler();
        try {
            if (sharedPreference.getIntValueWithKey(getApplicationContext(), "LocationDistenceInt") != 0) {
                LOCATION_DISTANCE = (float) sharedPreference.getIntValueWithKey(getApplicationContext(), "LocationDistenceInt");
            } else {
                sharedPreference.saveIntValueWithKey(getApplicationContext(), (int) LOCATION_DISTANCE, "LocationDistenceInt");
            }

            locationUpdateHandler.postDelayed(locationUpdateRunnable, 1000);
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
        Log.e(TAG, "onDestroy");
//        locationUpdateHandler = null;
        super.onDestroy();
        if (mLocationManager != null) {
//            for (int i = 0; i < mLocationListeners.length; i++) {
//                try {
//                    mLocationManager.removeUpdates(mLocationListeners[i]);
//                } catch (Exception ex) {
//                    Log.i(TAG, "fail to remove location listners, ignore", ex);
//                }
//            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void validateLatLogs(Location mlocation) {
        String Latitude, Longitude, fb_profileData;
        double dist;
        try {
            Latitude = sharedPreference.getValueWithKey(getApplicationContext(), "Latitude");
            Longitude = sharedPreference.getValueWithKey(getApplicationContext(), "Longitude");
            fb_profileData = sharedPreference.getValueWithKey(getApplicationContext(), "facebook_profileData");
            if (Latitude != null && Longitude != null) {
                if ((mlocation.getLatitude() != 0) && (mlocation.getLongitude() != 0)) {
//                     dist = calculateDistance(Double.parseDouble(Latitude),mlocation.getLatitude(), Double.parseDouble(Longitude),mlocation.getLongitude());
//                    if(dist<1)// Distance less than 1 kilometer then notify
                    {
                        sharedPreference.saveWithKey(getApplicationContext(), "" + mlocation.getLatitude(), "Latitude");
                        sharedPreference.saveWithKey(getApplicationContext(), "" + mlocation.getLongitude(), "Longitude");

                        Intent msgIntent = new Intent(getApplicationContext(), NearUserUpdateLocationIntentService.class);

                        msgIntent.putExtra("Latitude", "" + mlocation.getLatitude());
                        msgIntent.putExtra("Longitude", "" + mlocation.getLongitude());
                        msgIntent.putExtra("facebook_profileData", fb_profileData);

                        if (!isFromProfile) {
                            startService(msgIntent);
                        }
                    }
                }

            } else {
                if ((mlocation.getLatitude() != 0) && (mlocation.getLongitude() != 0)) {
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


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public double calculateDistance(double lat1, double lat2, double lon1,
                                    double lon2) {
        try {
            if (lat1 == 0.0 || lat2 == 0.0 || lat2 == 0.0 || lon2 == 0.0) {
                return 0.0;
            } else if (lat1 == lat2 && lon1 == lon2) {
                return 0.0;
            } else {
                int R = 6371; // Radius of the earth in km
                double dLat = Math.toRadians(lat2 - lat1); // Javascript
                // functions
                double dLon = Math.toRadians(lon2 - lon1);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(lat1))
                        * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double d = R * c;
                Log.e("CS", d + " ");
                return d * 1.609344;//converting to kilometers .
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }

    }

    @Override
    public void onResponseSuccess() {
        locationUpdateHandler.postDelayed(locationUpdateRunnable, LOCATION_INTERVAL);
    }
}