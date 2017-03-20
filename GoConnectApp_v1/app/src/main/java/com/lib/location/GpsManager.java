/*
 * GpsManager.java 
 */

package com.lib.location;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.lib.PrintLog;

/**
 * The Class GpsManager.
 */
public final class GpsManager {

	private static boolean isRunning;
	/** The location manager. */
	private static LocationManager locationManager;

	/** The location listeners. */
	static LocationListener coarseListener;
	static LocationListener fineListener;
	static LocationListener bestListener;

	private static Location currentLocation;

	static final int INTERVAL = 10000; // 10 Sec
	static final int FREQUENCY = 60000; // 1 min
	static final int DISTANCE = 50; // 50m

	/**
	 * Initialize & Start GPS service.
	 * 
	 * @param activity
	 *            the activity
	 */
	public static void startGps(Activity activity) {
		String coarseProvider;
		String fineProvider;
		String bestProvider;

		// if GPS has already been started then exit
		if (isRunning)
			return;

		/* Get the location manager */
		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

		// If location service is not available then exit
		if (locationManager == null)
			return;

		// set the gps service running flag to true
		isRunning = true;

		/* get the best location provider */
		// Low Accuracy Medium power consumption
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		coarseProvider = locationManager.getBestProvider(criteria, true);

		// High Accuracy High power consumption
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		fineProvider = locationManager.getBestProvider(criteria, true);

		// High Accuracy Low power consumption
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		bestProvider = locationManager.getBestProvider(criteria, true);

		coarseListener = new LocationListener() {
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				// on location changed set the new location as current location
				// and de-register the course listener
				currentLocation = location;

				if (locationManager != null) {
					if (coarseListener != null)
						locationManager.removeUpdates(coarseListener);
				}
			}
		};

		fineListener = new LocationListener() {
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				currentLocation = location;

				// on location changed set the new location as current location
				// if accuracy
				// is under 10 meters and de-register the course and high power
				// fine listeners
				if (location.getAccuracy() < 10) {
					if (locationManager != null) {
						if (fineListener != null)
							locationManager.removeUpdates(fineListener);
						if (coarseListener != null)
							locationManager.removeUpdates(coarseListener);
					}
				}
			}
		};

		bestListener = new LocationListener() {
			public void onStatusChanged(String provider, int status, Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}

			public void onLocationChanged(Location location) {
				// on location changed set the new location as current location
				// if accuracy is under 10 meters
				if (location.getAccuracy() < 10) {
					currentLocation = location;
				}
			}
		};

		// Register and start the location listeners
		if (bestProvider != null && bestListener != null) {
			locationManager.requestLocationUpdates(bestProvider, FREQUENCY, DISTANCE, bestListener);
		}
		if (coarseProvider != null && coarseListener != null) {
			locationManager.requestLocationUpdates(coarseProvider, 0, 0, coarseListener);
		}
		if (fineProvider != null && fineListener != null) {
			locationManager.requestLocationUpdates(fineProvider, 0, 0, fineListener);
		}

		new Thread() {
			public void run() {
				try {
					// since fineListener is on high power consumption mode
					// de-register it after INTERRVAL to conserve battery
					// coz if gps service is not avalibale the it will keep
					// running
					// and drain out the battery
					Thread.sleep(INTERVAL);
					if (locationManager != null && fineListener != null)
						locationManager.removeUpdates(fineListener);
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			};
		}.start();
		PrintLog.print("GPS", "GPS Started...");
	}

	/**
	 * Gets the Gps coordinates.
	 * 
	 * @return the gps coordinates
	 */
	public static Location getGps() {
		return currentLocation;
	}

	/**
	 * Stop GPS service
	 */
	public static void stopGPS() {
		// set GPS running flag to false
		isRunning = false;
		// de-registers the location listeners
		if (locationManager != null) {
			if (fineListener != null)
				locationManager.removeUpdates(fineListener);
			if (coarseListener != null)
				locationManager.removeUpdates(coarseListener);
			if (bestListener != null)
				locationManager.removeUpdates(bestListener);
		}

		// Release the static objects
		locationManager = null;
		coarseListener = null;
		fineListener = null;
		bestListener = null;
		currentLocation = null;

		PrintLog.print("GPS", "GPS Stopped...");
	}
}
