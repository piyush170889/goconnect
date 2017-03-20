/**
 * 
 */
package com.goconnect.events.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.devjiva.exception.UnauthorizedUserException;
import com.devjiva.goconnect.R;
import com.lib.DeviceInfo;
import com.lib.FileUtil;
import com.lib.PrintLog;

/**
 * @author Reddy
 * 
 */
public class Connection {

//	private static final String APIKEY = "eMed!rbykcdg1q6fivi6ec7o8xj2ll5xh0fjeoqyps5645opzskt76r";
//	private static final String SESSION_TOKEN = "eMed!rbykcdg1q6fivi6ec7o8xj2ll5xh0fjeoqyps5645opzskt76r!!3vdedk1qimwdp87s52bohfib5e5jype7xgwlohi9y7ywabydgp72xoiy41wtpsi1ypg7b4bhfxw6k33yij36aaggqaor5r5zjz8d";
	public static final String URL_BASE = "http://dev2.codecoop.net:9000/";
	// private static final String URL_BASE =
	// "https://demo.emedevents.com/Services/";

	private static final String URL_API_AUTHENTICATION_KEY = URL_BASE + "auth.json?";

	// http://demo.emedevents.com/Services/profile.json?type=messages&apiKey=eMed!rbykcdg1q6fivi6ec7o8xj2ll5xh0fjeoqyps5645opzskt76r&pageId=1&loggedinUserId=27015


	// http://demo.emedevents.com/Services/profile.json?type=notifications&apiKey=eMed!rbykcdg1q6fivi6ec7o8xj2ll5xh0fjeoqyps5645opzskt76r&pageId=1&loggedinUserId=27015
	private static final String URL_PROFILE_NOTIFICATIONS = URL_BASE + "profile.json?type=notifications&";

	// private static final String URL_EVENTS_UPCOMING = URL_BASE +
	// "type=upcoming&";



	private static final int EVENTS = 100;

	public static final int STATUS_FOUND = 200;
	public static final int STATUS_401 = 401;
	public static final int STATUS_402 = 402;
	public static final int STATUS_403 = 403;
	public static final int STATUS_404 = 404;
	public static final int STATUS_412 = 412;
	public static final int STATUS_423 = 423;
	public static final int STATUS_ERROR = 500;

	private static final String TAG = "Connection";

	private static HttpsURLConnection connection;

	/**
	 * authenticate device
	 * 
	 * @param activity
	 */
	public static Object authenticateDevice(Activity activity) {

		String appName = activity.getString(R.string.app_name);
		String appVersion = "1.0";

		try {
			PackageInfo packageInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
			appVersion = packageInfo.versionName;
		} catch (Exception e) {
			// ignore exception
		}
		String url = URL_API_AUTHENTICATION_KEY + "appName=" + appName + "&appVersion=" + appVersion + "&udid=" + DeviceInfo.getDeviceID(activity) + "&deviceToken=" + DeviceInfo.getDeviceID(activity)
				+ "&deviceName=" + DeviceInfo.getMake() + "&deviceModel=" + DeviceInfo.getModel() + "&deviceVersion=" + DeviceInfo.getOSVersion();
		url = url.replace(" ", "%20");
		return getResponse(url);
	}

	





	public static Object getResponse(String url) {

		try {
			String responseMessage = null;

			PrintLog.print(TAG, "Start....");
			PrintLog.print(TAG, "URL: " + url);

			// To allow all the SSL certificates from the server. This is to
			// bypass
			// the server certificate validation.

			// HttpsCertificate.allowAllSSL();
			// HttpURLConnection.setFollowRedirects(false);

			buildConnectionObject(url);

			connection.connect();

			int responseCode = connection.getResponseCode();
			responseMessage = connection.getResponseMessage();
			PrintLog.print(TAG, "Response code is: " + responseCode + ", Message: " + responseMessage);

			if (responseCode == STATUS_FOUND) {
				PrintLog.print(TAG, "Success....");
				String responseString = new String(FileUtil.toByteArray(connection.getInputStream()));
				PrintLog.print(TAG, responseString);
				return responseString;
			} else if(responseCode == STATUS_402) {
				/*Log.d(TAG, "Unauthoriz1ed User");
				String responseString = new String(FileUtil.toByteArray(connection.getInputStream()));
				Log.d(TAG, responseString);
				return responseString;*/
				throw new UnauthorizedUserException("Unauthorized User");
			} else {
				return new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e;
		}
	}

	/**
	 * Just to build the generic connection object
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private static void buildConnectionObject(String url) throws MalformedURLException, IOException {

		try {

			// Close previous connections if any
			if (connection != null) {
				connection.disconnect();
			}

			connection = (HttpsURLConnection) new URL(url).openConnection();

			buildBaseConnectionObject();
			connection.setDoOutput(false);
			connection.setRequestMethod("POST");

		} catch (Exception e) {
			PrintLog.print(TAG, "Exception" + e.getMessage());
		}

	}

	/**
	 * Build a connection object with base parameters and HtttHeaders
	 */
	private static void buildBaseConnectionObject() {

		connection.setRequestProperty("Connection", "keep-alive");
		connection.setRequestProperty("Content-Type", "application/json");
	}

	@SuppressWarnings("unused")
	public static Object callHttpPostRequestsV2(String url, JSONObject obj) {
		HttpURLConnection connection = null;
		url = url.replaceAll(" ", "%20");
		try {
			// Close previous connections if any
			if (connection != null) {
				connection.disconnect();
			}
			HttpURLConnection.setFollowRedirects(false);
			URL urlObject = new URL(url);
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Length", "" + obj.toString().getBytes().length);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestMethod("POST");

			// Send request as data output stream
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(obj.toString());
			wr.flush();
			wr.close();

			connection.connect();

			int responseCode = connection.getResponseCode();
			PrintLog.print(TAG,
					"Response code: " + connection.getResponseCode() + ", Message: " + connection.getResponseMessage());

			if (responseCode == STATUS_FOUND) {
				PrintLog.print(TAG, "Success....");
				return new String(FileUtil.toByteArray(connection.getInputStream()));
			} else if(responseCode == STATUS_402) {
				throw new UnauthorizedUserException("Unauthorized User");
			} else {
				return new Exception();
			}
		} catch (UnauthorizedUserException uue) {
			return uue;
		}catch (Exception e) {
			PrintLog.print(TAG, e.getMessage());
			return e;
		}


	}

	
	
	@SuppressWarnings("unused")
	public static String callHttpGetRequestsV2(String url) {
		HttpURLConnection connection = null;
		url = url.replaceAll(" ", "%20");
		try {
			// Close previous connections if any
			if (connection != null) {
				connection.disconnect();
			}
			connection = (HttpURLConnection) new URL(url).openConnection();
//			connection.setDoOutput(true);
			connection.setRequestMethod("GET");

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");


			connection.connect();

			int responseCode = connection.getResponseCode();

			if (responseCode == STATUS_FOUND) {
				PrintLog.print(TAG, "Success....");
//				return new String(FileUtil.toByteArray(connection.getInputStream()).toString());
				
				
				BufferedReader in = new BufferedReader(
				        new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();

				//print result
				System.out.println(response.toString());
				return response.toString();
				
			} else {
//				return new Exception();
				return "fail";
			}
		} catch (Exception e) {
			return "fail";
		}
	}

	@SuppressWarnings("unused")
	public static Object callHttpGetRequestsV2(String url, JSONObject obj) {
		HttpURLConnection connection = null;
		url = url.replaceAll(" ", "%20");
		try {
			// Close previous connections if any
			if (connection != null) {
				connection.disconnect();
			}
			HttpURLConnection.setFollowRedirects(false);
			URL urlObject = new URL(url);
			connection = (HttpURLConnection) urlObject.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Length", "" + obj.toString().getBytes().length);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			connection.setRequestMethod("GET");

			// Send request as data output stream
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(obj.toString());
			wr.flush();
			wr.close();

			connection.connect();

			int responseCode = connection.getResponseCode();
			PrintLog.print(TAG,
					"Response code: " + connection.getResponseCode() + ", Message: " + connection.getResponseMessage());

			if (responseCode == STATUS_FOUND) {
				PrintLog.print(TAG, "Success....");
				return new String(FileUtil.toByteArray(connection.getInputStream()));
			} else {
				return new Exception();
			}
		} catch (Exception e) {
			return e;
		}
	}

}
