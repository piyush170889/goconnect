package com.goconnect.events.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class SharedPreference {

	public static final String PREFS_NAME = "AOP_PREFS";
	public static final String PREFS_KEY = "AOP_PREFS_String";
	private String TAG="SharedPreference";

	public SharedPreference() {
		super();
	}

	public void save(Context context, String text) {
		SharedPreferences settings;
		Editor editor;
		
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
		editor = settings.edit(); //2

		editor.putString(PREFS_KEY, text); //3

		editor.commit(); //4
	}
	
	public void saveWithKey(Context context, String text,String key) {
		SharedPreferences settings;
		Editor editor;
		
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
		editor = settings.edit(); //2

		editor.putString(key, text); //3

		editor.commit(); //4
	}
	
	public void saveIntValueWithKey(Context context, int value,String key) {
		SharedPreferences settings;
		Editor editor;
		
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
		editor = settings.edit(); //2

		editor.putInt(key, value);//3

		editor.commit(); //4
	}
	
	public void saveBoolValueWithKey(Context context, boolean value,String key) {
		SharedPreferences settings;
		Editor editor;
		
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
		editor = settings.edit(); //2

		editor.putBoolean(key, value);//3
		Log.d(TAG, "saveBoolValueWithKey: "+key+":"+value);

		editor.commit(); //4
	}
	
	public int getIntValueWithKey(Context context,String key) {
		SharedPreferences settings;
		int value,defValue=0;

		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		value = settings.getInt(key, defValue);
		Log.d(TAG, "getIntValueWithKey: "+key+":"+value);
		return value;
	}
	
	public boolean getBoolValueWithKey(Context context,String key) {
		SharedPreferences settings;
		boolean value=false;

		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		value = settings.getBoolean(key, false);
		Log.d(TAG, "getBoolValueWithKey: "+key+":"+value);
		return value;
	}

	public String getValue(Context context) {
		SharedPreferences settings;
		String text;

		//settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		text = settings.getString(PREFS_KEY, null);
		return text;
	}
	
	public String getValueWithKey(Context context,String key) {
		SharedPreferences settings;
		String text;

		//settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		text = settings.getString(key, null);
		return text;
	}
	
	public void clearSharedPreference(Context context) {
		SharedPreferences settings;
		Editor editor;

		//settings = PreferenceManager.getDefaultSharedPreferences(context);
		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		editor = settings.edit();

		editor.clear();
		editor.commit();
	}

	public void removeValue(Context context) {
		SharedPreferences settings;
		Editor editor;

		settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		editor = settings.edit();
		editor.remove(PREFS_KEY);
		editor.commit();
	}	
}