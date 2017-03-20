/*
 * PrintLog.java 
 */

package com.lib;

import android.util.Log;

import com.devjiva.goconnect.BuildConfig;

/**
 * This class is a util class to print and maintain log from central location.
 * 
 * @author sekhar.jonnalagadda
 */
public class PrintLog {

	/**
	 * This is to print error log. this is same as default Log.e
	 * 
	 * @param TAG
	 *            generally a class name from where it is called
	 * @param description
	 *            description of the message to the user log
	 * @param e
	 *            The occurred error
	 */
	public static void print(String TAG, String description, Exception e) {
		if (BuildConfig.DEBUG) {
			if (description != null) {
				Log.v(TAG, description);
			}
			try {
				e.printStackTrace();
			} catch (Exception e1) {
			}
		}
	}

	/**
	 * This is to print verbose log. this is same as default Log.v
	 * 
	 * @param TAG
	 *            generally a class name from where it is called
	 * @param description
	 *            description of the message to the user log
	 */
	public static void print(String TAG, String description) {
		if (BuildConfig.DEBUG && description != null) {
			Log.v(TAG, description);
		}
	}
}
