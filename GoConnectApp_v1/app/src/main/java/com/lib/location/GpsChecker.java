package com.lib.location;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;

import com.devjiva.goconnect.R;
import com.lib.DialogBox;

public final class GpsChecker {

	/**
	 * Check GPS is available or not
	 */
	public static void enableGPS(final Activity activity, String message) {
		DialogBox newFragment = DialogBox.newInstance(message);
		newFragment.show(((AppCompatActivity) activity).getSupportFragmentManager(), DialogBox.TAG_DIALOG);
		newFragment.setPositiveButton(activity.getString(R.string.action_settings), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				activity.startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
				dialog.dismiss();
			}
		});
		newFragment.setNegativeButton(activity.getString(android.R.string.cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	public static boolean isGpsEnabled(Activity activity) {
		// get location manager to check gps availability
		LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

		boolean isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		return (isGPS || isNetwork);
	}

}
