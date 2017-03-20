/*
 * DeviceInfo.java 
 */

package com.lib;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

public final class DeviceInfo {
	public static final String getDeviceID(Context context) {
		String deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return (deviceId == null ? "Emulator" : deviceId); // null check for
															// emulators
	}

	public static final String getMake() {
		return Build.MANUFACTURER;
	}

	public static final String getModel() {
		return Build.MODEL;
	}

	public static final String getAppType() {
		return "Native";
	}

	public static final String getAccessedFrom() {
		return "Android";
	}

	public static final String getOSVersion() {
		return "Android " + Build.VERSION.RELEASE;
	}

	@SuppressWarnings("deprecation")
	public static final String getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getWidth() + "";
	}

	@SuppressWarnings("deprecation")
	public static final String getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		return display.getHeight() + "";
	}

	public static final String getBrowserType() {
		return "Android Browser";
	}

	public static final String getCarrier(Context context) {
		return ((TelephonyManager) context.getSystemService(Activity.TELEPHONY_SERVICE)).getNetworkOperatorName();
	}
}
