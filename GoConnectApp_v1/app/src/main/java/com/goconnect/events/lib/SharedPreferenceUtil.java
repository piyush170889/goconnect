package com.goconnect.events.lib;
///**
// * 
// */
//package com.emed.events.lib;
//
//import java.util.ArrayList;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.preference.PreferenceManager;
//
//import com.emed.events.model.Speciality;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//
///**
// * @author Reddy
// * 
// */
//public class SharedPreferenceUtil {
//
//	private static SharedPreferences getSharedPreference(Context context) {
//		return PreferenceManager.getDefaultSharedPreferences(context);
//	}
//
//	public static ArrayList<Speciality> getGuestSpecialties(Context context) {
//		ArrayList<Speciality> list = new ArrayList<>();
//		try {
//			Gson gson = new Gson();
//			SharedPreferences prefs = getSharedPreference(context);
//			String json = prefs.getString(Globals.GUEST_SPECIALTIES, "");
//			list = gson.fromJson(json, new TypeToken<ArrayList<Speciality>>() {
//			}.getType());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		if (list == null) {
//			list = new ArrayList<>();
//		}
//		return list;
//	}
//
//	public static void saveGuestSpecialties(Context context, ArrayList<Speciality> specialties) {
//		try {
//			Gson gson = new Gson();
//			String jSon = gson.toJson(specialties);
//
//			SharedPreferences prefs = getSharedPreference(context);
//			Editor editor = prefs.edit();
//			editor.putString(Globals.GUEST_SPECIALTIES, jSon);
//			editor.commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//}
