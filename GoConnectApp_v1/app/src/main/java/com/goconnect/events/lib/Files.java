/**
 * 
 */
package com.goconnect.events.lib;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

/**
 * @author Reddy
 * 
 */
public class Files {

//	public static boolean saveEvent(Activity activity, EventDetails event) {
//		try {
//			ArrayList<EventDetails> events = new ArrayList<>();
//			events = getEvents(activity);
//
//			events.add(event);
//			String jsonFormat = new Gson().toJson(events);
//
//			writeToFile(activity, jsonFormat);
//
//			Toast.makeText(activity, "Saved / Removed Successful.", Toast.LENGTH_LONG).show();
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}
//
//	public static ArrayList<EventDetails> getEvents(Activity activity) {
//		String jsonData = readFromFile(activity);
//		Gson gson = new Gson();
//		ArrayList<EventDetails> eventDetails = new ArrayList<>();
//		eventDetails = gson.fromJson(jsonData, new TypeToken<ArrayList<EventDetails>>() {
//		}.getType());
//
//		return eventDetails;
//	}

	public static void writeToFile(Activity activity, String data) {
		try {
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(activity.openFileOutput(Globals.FILE_NAME, Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e) {
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	public static String readFromFile(Activity activity) {

		String jsonString = "";

		try {
			InputStream inputStream = activity.openFileInput(Globals.FILE_NAME);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				jsonString = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("login activity", "Can not read file: " + e.toString());
		}

		return jsonString;
	}

}
