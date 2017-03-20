/**
 * 
 */
package com.goconnect.events.lib;

/**
 * @author Reddy
 * 
 */
public class Globals {

	public static final String LOGIN_SPF_NAME = "LOGIN_GOCONNECT_SPF";
	public static final String LOADING = "Loading...";
	public static final String UPDATING = "Updating...";

	public static final String LOADING_COUNTRIES = "Loading Countries...";
	public static final String LOADING_STATES = "Loading States...";
	public static final String LOADING_CITIES = "Loading Cities...";

	/** Connection error messages */
	public static final String ALERT_NETWORK_NOT_FOUND = "A network communication error occurred. Please try again later."; // /
	public static final String ALERT_NOT_ALLOWED_TO_LOGIN = "You are not allowed to login at this point.";
	public static final String ALERT_500 = "Unable to connect to server at this time. Please try again later.";// /
	public static final String ALERT_SERVER_ERROR = "Unable to connect to server.  Please try again later."; // /
	public static final String ALERT_UNEXPECTED_ERROR = "An unexpected error occured.";

	public static final String ALERT_401_403 = "You are not allowed to login at this point.";
	public static final String ALERT_412 = "Invalid Request Parameters.";

	public static final String ALERT_403_EXCEPT_LOGIN = "You do not have access to this feature at this point.";
	public static final String ALERT_NO_RESULTS1 = "Select atleast ine Speciality";

	public static final String ALERT_NO_RESULTS = "No results found.";

	public static final int RESPONSE_EVENTS = 100;
	public static final int RESPONSE_EVENT_DETAILS = 101;
	public static final int RESPONSE_GET_TICKETS = 102;
	public static final int RESPONSE_ABOUT_US = 103;
	public static final int RESPONSE_PROFILE_SPEAKER = 104;
	public static final int RESPONSE_SPECIALTIES = 105;
	public static final int RESPONSE_USER_SPECIALTIES = 106;
	public static final int RESPONSE_USER_PROFILE = 107;
	public static final int RESPONSE_CITIES = 108;
	public static final int RESPONSE_STATES = 109;
	public static final int RESPONSE_COUNTRIES = 110;
	public static final int RESPONSE_MESSAGES = 111;
	public static final int RESPONSE_NOTIFICATIONS = 112;
	public static final int RESPONSE_MY_TICKETS = 113;
	public static final int RESPONSE_PROFILE_ATTENDEE = 114;

	public static final int MY_EVENTS_UPCOMING = 1;
	public static final int MY_EVENTS_TRACKING = 2;
	public static final int MY_EVENTS_ATTENDING = 3;
	public static final int MY_EVENTS_NEARBY = 4;
	public static final int EVENTS_BY_CONFERENCE = 4;
	public static final int EVENTS_BY_LOCATION = 5;
	public static final int EVENTS_BY_SPECIALITY = 6;
	public static final int EVENTS_BY_CITY = 7;

	public static final int TYPE_TRACK = 100;
	public static final int TYPE_ATTEND = 101;

	public static final int STOP = 0;
	public static final int START = 1;

	public static final String DATA = "Data";
	public static final boolean GUEST = true;

	public static final String SPECIALTIES_OFFLINE = "SPECIALTIES_OFFLINE";
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final String SESSION_TOKEN = "SESSION_TOKEN";
	public static final String FULL_NAME = "FULL_NAME";
	public static final String USER_ID = "USER_ID";
	public static final String EMAIL_ID = "EMAIL_ID";
	public static final String ACCESS_EXPIRY = "access_expires";
	public static final String GUEST_SPECIALTIES = "GUEST_SPECIALTIES";

	public static final String CONFIRM_EXIT = "Are you sure you want to exit from the application?";
	public static final String CONFIRM_LOGOUT = "Are you sure you want to logout?";

	public static final int EVENTS_ALL = 11;
	public static final int EVENTS_SPECIALITY = 12;
	public static final int EVENTS_NEARBY = 13;
	public static final int EVENTS_FRIENDS = 14;

	public static final int TYPE_ATTENDEES = 1000;
	public static final int TYPE_SPEAKERS = 1001;

	public static final String FILE_NAME = "EMedSavedEvents";

	public static final String TABLE_EVENTS = "TABLE_EVENTS";
	public static final String TABLE_EVENT_DETAILS = "TABLE_EVENT_DETAILS";

	public static final String ALERT_NO_LOCATION = "Location services are disabled. To use your current location, enable this feature in your Settings."; // /

	public static final int COUNTRY = 1;
	public static final int STATE = 2;
	public static final int CITY = 3;

	public static final String SCR = "SCR";
	public static final int SCR_REGISTER = 100;
	public static final int SCR_SPECIALTY = 101;

}
