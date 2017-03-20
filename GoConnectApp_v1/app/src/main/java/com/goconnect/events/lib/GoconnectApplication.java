/**
 * 
 */
package com.goconnect.events.lib;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * @author Reddy
 * 
 */
public class GoconnectApplication extends Application {

	private String accessToken;
	private String sessionToken;
	private String fullName;
	private String userId;
	private String email;
	private long accessExpiry;
//	private ArrayList<Speciality> specialties;
	private boolean isMainUpdateRequired = false;

	@Override
	public void onCreate() {
		super.onCreate();

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		accessToken = prefs.getString(Globals.ACCESS_TOKEN, null);
		sessionToken = prefs.getString(Globals.SESSION_TOKEN, null);
		fullName = prefs.getString(Globals.FULL_NAME, null);
		userId = prefs.getString(Globals.USER_ID, null);
		email = prefs.getString(Globals.EMAIL_ID, null);
		accessExpiry = prefs.getLong(Globals.ACCESS_EXPIRY, 0);
	}

	/**
	 * @return the fullName
	 */
	public final String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName
	 *            the fullName to set
	 */
	public final void setFullName(String fullName) {
		this.fullName = fullName;
		saveValue(Globals.FULL_NAME, fullName);
	}

	/**
	 * @return the sessionToken
	 */
	public String getSessionToken() {
		return sessionToken;
	}

	/**
	 * @param sessionToken
	 *            the sessionToken to set
	 */
	public void setSessionToken(String sessionToken) {
		this.sessionToken = sessionToken;
		saveValue(Globals.SESSION_TOKEN, sessionToken);
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @return the isMainUpdateRequired
	 */
	public final boolean isMainUpdateRequired() {
		return isMainUpdateRequired;
	}

	/**
	 * @param isMainUpdateRequired
	 *            the isMainUpdateRequired to set
	 */
	public final void setMainUpdateRequired(boolean isMainUpdateRequired) {
		this.isMainUpdateRequired = isMainUpdateRequired;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
		saveValue(Globals.EMAIL_ID, email);
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * @param accessToken
	 *            the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
		saveValue(Globals.ACCESS_TOKEN, accessToken);
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
		saveValue(Globals.USER_ID, userId);
	}

	public boolean isAccessTokenExist() {
		if (accessToken == null)
			return false;

		return true;
	}

	public void clearToken() {
		setAccessToken(null);
	}

	/**
	 * @param tag
	 * @param value
	 */
	private void saveValue(String tag, String value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.putString(tag, value);
		edit.commit();
	}

	/**
	 * @param tag
	 * @param value
	 */
	private void saveValue(String tag, long value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		edit.putLong(tag, value);
		edit.commit();
	}

	/**
	 * get String from Shared preference
	 * 
	 * @param tag
	 * @return
	 */
	private String getString(String tag) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getString(tag, "");
	}

	public void clearCredentials() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final Editor edit = prefs.edit();
		// edit.clear();
		// edit.remove(Globals.ACCESS_TOKEN);
		// edit.remove(Globals.SESSION_TOKEN);
		// edit.remove(Globals.USER_ID);
		// edit.remove(Globals.EMAIL_ID);
		edit.clear();
		edit.commit();
		clearToken();
	}

	/**
	 * @return the accessExpiry
	 */
	public long getAccessExpiry() {
		return accessExpiry;
	}

	/**
	 * @param accessExpiry
	 *            the accessExpiry to set
	 */
	public void setAccessExpiry(long accessExpiry) {
		this.accessExpiry = accessExpiry;
		saveValue(Globals.ACCESS_EXPIRY, accessExpiry);
	}

}