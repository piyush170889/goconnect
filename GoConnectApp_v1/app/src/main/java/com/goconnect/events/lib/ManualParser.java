package com.goconnect.events.lib;
///**
// * 
// */
//package com.emed.events.lib;
//
//import java.util.ArrayList;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.emed.events.model.AboutUs;
//import com.emed.events.model.Attendee;
//import com.emed.events.model.City;
//import com.emed.events.model.Corporate;
//import com.emed.events.model.Country;
//import com.emed.events.model.Event;
//import com.emed.events.model.EventDetails;
//import com.emed.events.model.Management;
//import com.emed.events.model.Messages;
//import com.emed.events.model.MyEventsResponse;
//import com.emed.events.model.Partner;
//import com.emed.events.model.ProfileAttendee;
//import com.emed.events.model.ProfileSpeaker;
//import com.emed.events.model.SessionCoverage;
//import com.emed.events.model.ShowTickets;
//import com.emed.events.model.Speaker;
//import com.emed.events.model.Speciality;
//import com.emed.events.model.State;
//import com.emed.events.model.Ticket;
//import com.emed.events.model.UserProfile;
//import com.emed.events.model.Venue;
//import com.lib.PrintLog;
//
///**
// * @author Reddy
// * 
// */
//public class ManualParser {
//
//	private static final String TAG = "ManualParser";
//
//	public static final Object parseJsonToObject(String result, int type) {
//		switch (type) {
//		case Globals.RESPONSE_EVENTS:
//			return parseEvents(result);
//		case Globals.RESPONSE_EVENT_DETAILS:
//			return parseEventDetails(result);
//		case Globals.RESPONSE_GET_TICKETS:
//			return parseGetTickets(result);
//		case Globals.RESPONSE_ABOUT_US:
//			return parseAboutUs(result);
//		case Globals.RESPONSE_PROFILE_SPEAKER:
//			return parseSpekerProfile(result);
//		case Globals.RESPONSE_PROFILE_ATTENDEE:
//			return parseAttendeeProfile(result);
//		case Globals.RESPONSE_SPECIALTIES:
//			return parseSpecialities(result);
//		case Globals.RESPONSE_USER_PROFILE:
//			return parseUserProfile(result);
//		case Globals.RESPONSE_USER_SPECIALTIES:
//			return parseSpecialities(result);
//		case Globals.RESPONSE_MESSAGES:
//			return parseMessages(result);
//		case Globals.RESPONSE_MY_TICKETS:
//			return parseMyTickets(result);
//		case Globals.RESPONSE_COUNTRIES:
//			return parseCountries(result);
//		case Globals.RESPONSE_STATES:
//			return parseStates(result);
//		case Globals.RESPONSE_CITIES:
//			return parseCities(result);
//		default:
//			return null;
//		}
//	}
//
//	private static Object parseMessages(String result) {
//		try {
//			ArrayList<Messages> messages = new ArrayList<>();
//			JSONObject obj = new JSONObject(result.toString());
//			if (obj.has("record")) {
//				JSONObject record = obj.getJSONObject("record");
//				int scollstatedeside = 0;
//
//				// looping through All records
//				for (int i = 0; i < record.length() - 1; i++) {
//					Messages message = new Messages();
//					scollstatedeside = scollstatedeside + 1;
//					String s = Integer.toString(i);
//					JSONObject messageObj = record.getJSONObject(s);
//					message.setMessage(getString(messageObj, "message"));
//					message.setTime(getString(messageObj, "date"));
//					messages.add(message);
//				}
//				return messages;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private static Object parseMyTickets(String data) {
//		try {
//			ArrayList<Ticket> tickets = new ArrayList<>();
//			JSONObject obj = new JSONObject(data.toString());
//			if (obj.has("record")) {
//				JSONObject record = obj.getJSONObject("record");
//				int scollstatedeside = 0;
//
//				// looping through All records
//				for (int i = 0; i < record.length() - 1; i++) {
//					Ticket ticket = new Ticket();
//					scollstatedeside = scollstatedeside + 1;
//					String s = Integer.toString(i);
//					JSONObject ticketsObj = record.getJSONObject(s);
//					ticket.setConfTitle(getString(ticketsObj, "conference_title"));
//					ticket.setName(getString(ticketsObj, "name"));
//					ticket.setPaidDate(getString(ticketsObj, "paid_date"));
//					ticket.setInvoiceNumber(getString(ticketsObj, "invoice_number"));
//					ticket.setPaymentType(getString(ticketsObj, "payment_type"));
//					ticket.setPaidAmount(getString(ticketsObj, "paid_amount"));
//					ticket.setNumberOfTickets(getString(ticketsObj, "no_of_tickets"));
//					ticket.setPaymentStatus(getString(ticketsObj, "payment_status"));
//					ticket.setTicketName(getString(ticketsObj, "ticket_name"));
//
//					tickets.add(ticket);
//				}
//				return tickets;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private static Object parseSpecialities(String data) {
//		try {
//			ArrayList<Speciality> specialities = new ArrayList<>();
//			JSONObject result = new JSONObject(data.toString());
//			if (result.has("record")) {
//				JSONObject record = result.getJSONObject("record");
//				int scollstatedeside = 0;
//
//				// looping through All records
//				for (int i = 0; i < record.length() - 1; i++) {
//					Speciality speciality = new Speciality();
//
//					scollstatedeside = scollstatedeside + 1;
//					String s = Integer.toString(i);
//					JSONObject specialityObj = record.getJSONObject(s);
//					speciality.setSpecialityName(getString(specialityObj, "specialityName"));
//					if (speciality.getSpecialityName().equals("")) {
//						speciality.setSpecialityName(getString(specialityObj, "name"));
//					}
//					speciality.setSpecialityId(Integer.parseInt(getString(specialityObj, "specialityId")));
//					specialities.add(speciality);
//				}
//			}
//			return specialities;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return e;
//		}
//	}
//
//	private static Object parseSpekerProfile(String result) {
//		try {
//			ProfileSpeaker speakerInfo = new ProfileSpeaker();
//
//			JSONObject json = new JSONObject(result);
//
//			if (json.has("record")) {
//				JSONObject record = json.getJSONObject("record");
//
//				speakerInfo.setName(getString(record, "firstName") + " " + getString(record, "lastName"));
//				speakerInfo.setImageURl(getString(record, "image"));
//
//				if (record.has("ConfData")) {
//					JSONObject confData = record.getJSONObject("ConfData");
//					if (confData.has("upcoming")) {
//						ArrayList<Event> upcomingEvents = new ArrayList<>();
//
//						JSONArray upcoming = confData.getJSONArray("upcoming");
//
//						int scollstatedeside = 0;
//
//						// looping through All records
//						for (int i = 0; i < upcoming.length() - 1; i++) {
//							Event event = new Event();
//
//							// scollstatedeside = scollstatedeside + 1;
//							// String s = Integer.toString(i);
//							JSONObject eventJsonOject = upcoming.getJSONObject(i);
//							event.setTitle(getString(eventJsonOject, "title"));
//							event.setStartdate(getString(eventJsonOject, "startdate"));
//							event.setEnddate(getString(eventJsonOject, "enddate"));
//							event.setAddress(getString(eventJsonOject, "address"));
//							event.setPlace(getString(eventJsonOject, "place"));
//							event.setCityName(getString(eventJsonOject, "cityName"));
//							event.setLatitude(getString(eventJsonOject, "latitude"));
//							event.setLongitude(getString(eventJsonOject, "longitude"));
//
//							JSONArray specialities = eventJsonOject.getJSONArray("speciality");
//
//							ArrayList<Speciality> specialitiesArray = new ArrayList<Speciality>();
//							Speciality speciality = new Speciality();
//							for (int n1 = 0; n1 < specialities.length(); n1++) {
//								JSONObject obj = specialities.getJSONObject(n1);
//
//								speciality.setSpecialityId(getInt(obj, "specialityId"));
//								speciality.setSpecialityName(getString(obj, "specialityName"));
//								specialitiesArray.add(speciality);
//							}
//							upcomingEvents.add(event);
//						}
//						speakerInfo.setUpcomingEvents(upcomingEvents);
//					} else if (confData.has("past")) {
//						ArrayList<Event> pastEvents = new ArrayList<>();
//
//						JSONArray past = confData.getJSONArray("past");
//
//						// int scollstatedeside = 0;
//
//						// looping through All records
//						for (int i = 0; i < past.length() - 1; i++) {
//							Event event = new Event();
//
//							// scollstatedeside = scollstatedeside + 1;
//							// String s = Integer.toString(i);
//							JSONObject eventJsonOject = past.getJSONObject(i);
//							event.setTitle(getString(eventJsonOject, "title"));
//							event.setStartdate(getString(eventJsonOject, "startdate"));
//							event.setEnddate(getString(eventJsonOject, "enddate"));
//							event.setAddress(getString(eventJsonOject, "address"));
//							event.setPlace(getString(eventJsonOject, "place"));
//							event.setCityName(getString(eventJsonOject, "cityName"));
//							event.setLatitude(getString(eventJsonOject, "latitude"));
//							event.setLongitude(getString(eventJsonOject, "longitude"));
//
//							JSONArray specialities = eventJsonOject.getJSONArray("speciality");
//
//							ArrayList<Speciality> specialitiesArray = new ArrayList<Speciality>();
//							Speciality speciality = new Speciality();
//							for (int n1 = 0; n1 < specialities.length(); n1++) {
//								JSONObject obj = specialities.getJSONObject(n1);
//
//								speciality.setSpecialityId(getInt(obj, "specialityId"));
//								speciality.setSpecialityName(getString(obj, "specialityName"));
//								specialitiesArray.add(speciality);
//							}
//							pastEvents.add(event);
//						}
//						speakerInfo.setPastEvents(pastEvents);
//					} else if (confData.has("ongoing")) {
//						ArrayList<Event> ongoingEvents = new ArrayList<>();
//
//						JSONArray ongoing = confData.getJSONArray("ongoing");
//
//						int scollstatedeside = 0;
//
//						// looping through All records
//						for (int i = 0; i < ongoing.length() - 1; i++) {
//							Event event = new Event();
//
//							// scollstatedeside = scollstatedeside + 1;
//							// String s = Integer.toString(i);
//
//							JSONObject eventJsonOject = ongoing.getJSONObject(i);
//							event.setTitle(getString(eventJsonOject, "title"));
//							event.setStartdate(getString(eventJsonOject, "startdate"));
//							event.setEnddate(getString(eventJsonOject, "enddate"));
//							event.setAddress(getString(eventJsonOject, "address"));
//							event.setPlace(getString(eventJsonOject, "place"));
//							event.setCityName(getString(eventJsonOject, "cityName"));
//							event.setLatitude(getString(eventJsonOject, "latitude"));
//							event.setLongitude(getString(eventJsonOject, "longitude"));
//
//							JSONArray specialities = eventJsonOject.getJSONArray("speciality");
//
//							ArrayList<Speciality> specialitiesArray = new ArrayList<Speciality>();
//							Speciality speciality = new Speciality();
//							for (int n1 = 0; n1 < specialities.length(); n1++) {
//								JSONObject obj = specialities.getJSONObject(n1);
//
//								speciality.setSpecialityId(getInt(obj, "specialityId"));
//								speciality.setSpecialityName(getString(obj, "specialityName"));
//								specialitiesArray.add(speciality);
//							}
//							ongoingEvents.add(event);
//						}
//						speakerInfo.setOngoingEvents(ongoingEvents);
//					}
//				}
//				if (record.has("SessionCoverage")) {
//					ArrayList<SessionCoverage> sessionCoverages = new ArrayList<>();
//
//					JSONArray sessionCoverageArray = record.getJSONArray("SessionCoverage");
//
//					for (int i = 0; i < sessionCoverageArray.length(); i++) {
//
//						SessionCoverage sessionCoverage = new SessionCoverage();
//						JSONObject sessionObject = sessionCoverageArray.getJSONObject(i);
//						sessionCoverage.setType(getString(sessionObject, "type"));
//						sessionCoverage.setTypeKey(getInt(sessionObject, "type_key"));
//						sessionCoverage.setCount(getInt(sessionObject, "count"));
//						sessionCoverages.add(sessionCoverage);
//					}
//					speakerInfo.setSessionCoverage(sessionCoverages);
//				}
//			}
//			return speakerInfo;
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return e;
//		}
//	}
//
//	/**
//	 * Parse Attendee Profile
//	 * 
//	 * @param result
//	 * @return
//	 */
//	private static Object parseAttendeeProfile(String result) {
//		try {
//			ProfileAttendee attendeeInfo = new ProfileAttendee();
//
//			JSONObject json = new JSONObject(result);
//
//			if (json.has("record")) {
//				JSONObject record = json.getJSONObject("record");
//
//				attendeeInfo.setName(getString(record, "firstName") + " " + getString(record, "lastName"));
//				attendeeInfo.setSpeakerId(getString(record, "SpeakerId"));
//				attendeeInfo.setUserId(getString(record, "userId"));
//				attendeeInfo.setUserName(getString(record, "userName"));
//				attendeeInfo.setEmail(getString(record, "email"));
//				attendeeInfo.setContactNo(getString(record, "contactno"));
//				attendeeInfo.setDesignation(getString(record, "designation"));
//				attendeeInfo.setHospitalName(getString(record, "hospitalname"));
//				attendeeInfo.setAddress(getString(record, "address"));
//				attendeeInfo.setCityID(getString(record, "cityId"));
//				attendeeInfo.setStateId(getString(record, "stateId"));
//				attendeeInfo.setCountryId(getString(record, "countryId"));
//				attendeeInfo.setCityName(getString(record, "cityName"));
//				attendeeInfo.setImage(getString(record, "image"));
//				attendeeInfo.setBiography(getString(record, "biography"));
//
//				if (record.has("SessionCoverage")) {
//					ArrayList<SessionCoverage> sessionCoverages = new ArrayList<>();
//
//					JSONArray sessionCoverageArray = record.getJSONArray("SessionCoverage");
//
//					for (int i = 0; i < sessionCoverageArray.length(); i++) {
//
//						SessionCoverage sessionCoverage = new SessionCoverage();
//						JSONObject sessionObject = sessionCoverageArray.getJSONObject(i);
//						sessionCoverage.setType(getString(sessionObject, "type"));
//						sessionCoverage.setTypeKey(getInt(sessionObject, "type_key"));
//						sessionCoverage.setCount(getInt(sessionObject, "count"));
//						sessionCoverages.add(sessionCoverage);
//					}
//					attendeeInfo.setSessionCoverage(sessionCoverages);
//				}
//			}
//			return attendeeInfo;
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return e;
//		}
//	}
//
//	/**
//	 * parse Aboutus
//	 * 
//	 * @param result
//	 * @return
//	 */
//	private static Object parseAboutUs(String result) {
//		try {
//			AboutUs aboutUs = new AboutUs();
//
//			JSONObject json = new JSONObject(result);
//			aboutUs.setAboutUs(getString(json, "aboutus"));
//
//			if (json.has("mamagement")) {
//				ArrayList<Management> managers = new ArrayList<>();
//				JSONObject obj = json.getJSONObject("mamagement");
//
//				aboutUs.setNameManagement(getString(obj, "Name"));
//				JSONArray managementArray = obj.getJSONArray("management");
//				for (int i = 0; i < managementArray.length(); i++) {
//					Management management = new Management();
//					JSONObject mngmnt = managementArray.getJSONObject(i);
//					management.setName(getString(mngmnt, "Name"));
//					management.setDesignation(getString(mngmnt, "Desig"));
//					management.setAbout(getString(mngmnt, "About"));
//					management.setImage(getString(mngmnt, "image"));
//					managers.add(management);
//				}
//				aboutUs.setManagementTeam(managers);
//			}
//			if (json.has("corporate")) {
//				ArrayList<Corporate> corporates = new ArrayList<>();
//				JSONObject obj = json.getJSONObject("corporate");
//
//				aboutUs.setNameCorporate(getString(obj, "Name"));
//				JSONArray corporatesArray = obj.getJSONArray("Advisory BoardMembers");
//				for (int i = 0; i < corporatesArray.length(); i++) {
//					Corporate corporate = new Corporate();
//					JSONObject corp = corporatesArray.getJSONObject(i);
//					corporate.setName(getString(corp, "Name"));
//					corporate.setDesignation(getString(corp, "Desig"));
//					corporate.setAbout(getString(corp, "About"));
//					corporate.setImage(getString(corp, "image"));
//					corporates.add(corporate);
//				}
//				aboutUs.setCorporateTeam(corporates);
//			}
//			if (json.has("partners")) {
//				ArrayList<Partner> partners = new ArrayList<>();
//				JSONObject obj = json.getJSONObject("partners");
//
//				aboutUs.setNamePartners(getString(obj, "Name"));
//				JSONArray partnersArray = obj.getJSONArray("partners");
//				for (int i = 0; i < partnersArray.length(); i++) {
//					Partner partner = new Partner();
//					JSONObject corp = partnersArray.getJSONObject(i);
//					partner.setName(getString(corp, "Name"));
//					partner.setAbout(getString(corp, "Desc"));
//					partner.setImage(getString(corp, "image"));
//					partners.add(partner);
//				}
//				aboutUs.setParterns(partners);
//			}
//			return aboutUs;
//		} catch (JSONException e) {
//			e.printStackTrace();
//			return e;
//		}
//	}
//
//	private static Object parseEvents(String result) {
//		MyEventsResponse response = new MyEventsResponse();
//
//		try {
//
//			JSONObject reader = new JSONObject(result);
//			response.setStatusCode(getInt(reader, "statusCode"));
//			response.setStatus(getString(reader, "status"));
//			response.setTotalCnt(getInt(reader, "totalCnt"));
//
//			if (reader.has("record")) {
//				ArrayList<Event> events = new ArrayList<>();
//
//				JSONObject records = reader.getJSONObject("record");
//
//				PrintLog.print(TAG, "eve>>>" + records.length());
//
//				int scollstatedeside = 0;
//
//				// looping through All Contacts
//				for (int i = 0; i < records.length() - 1; i++) {
//					Event event = new Event();
//
//					scollstatedeside = scollstatedeside + 1;
//					String s = Integer.toString(i);
//					JSONObject eventJsonOject = records.getJSONObject(s);
//					event.setConferenceId(getString(eventJsonOject, "conferenceId"));
//					event.setTitle(getString(eventJsonOject, "title"));
//					event.setStartdate(getString(eventJsonOject, "startdate"));
//					event.setEnddate(getString(eventJsonOject, "enddate"));
//					event.setAddress(getString(eventJsonOject, "address"));
//					event.setPlace(getString(eventJsonOject, "place"));
//					event.setCityId(getString(eventJsonOject, "cityId"));
//					event.setStateId(getString(eventJsonOject, "stateId"));
//					event.setCountryId(getString(eventJsonOject, "countryId"));
//					event.setCityName(getString(eventJsonOject, "cityName"));
//					event.setStateName(getString(eventJsonOject, "stateName"));
//					event.setCountryName(getString(eventJsonOject, "countryName"));
//					event.setLatitude(getString(eventJsonOject, "latitude"));
//					event.setLongitude(getString(eventJsonOject, "longitude"));
//					event.setConfLogo(getString(eventJsonOject, "ConfLogo"));
//
//					JSONArray specialities = eventJsonOject.getJSONArray("speciality");
//
//					ArrayList<Speciality> specialitiesArray = new ArrayList<Speciality>();
//					for (int n1 = 0; n1 < specialities.length(); n1++) {
//						Speciality speciality = new Speciality();
//						JSONObject obj = specialities.getJSONObject(n1);
//
//						speciality.setSpecialityId(getInt(obj, "specialityId"));
//						speciality.setSpecialityName(getString(obj, "specialityName"));
//						specialitiesArray.add(speciality);
//					}
//					event.setSpecialities(specialitiesArray);
//
//					events.add(event);
//				}
//				response.setEvents(events);
//				return response;
//			} else {
//				return null;
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//			try {
//				JSONObject reader = new JSONObject(result);
//				response.setErrorMessage(getString(reader, "errMsg"));
//				return response;
//			} catch (JSONException e1) {
//				e1.printStackTrace();
//				return e1;
//			}
//		}
//	}
//
//	// This is to avoid JSon exception in case the key is not available in
//	// response json
//	public static String getString(JSONObject eventJsonOject, String key) {
//		try {
//			return eventJsonOject.getString(key);
//		} catch (Exception e) {
//			return "";
//		}
//	}
//
//	// This is to avoid JSon exception in case the key is not available in
//	// response json
//	public static int getInt(JSONObject eventJsonOject, String key) {
//		try {
//			return eventJsonOject.getInt(key);
//		} catch (Exception e) {
//			return 0;
//		}
//	}
//
//	private static Object parseEventDetails(String result) {
//		EventDetails event = new EventDetails();
//
//		try {
//			JSONObject reader = new JSONObject(result);
//			JSONObject records = (JSONObject) reader.get("record");
//
//			int scollstatedeside = 0;
//
//			// looping through All Contacts
//			for (int i = 0; i < records.length() - 1; i++) {
//
//				scollstatedeside = scollstatedeside + 1;
//				String s = Integer.toString(i);
//
//				JSONObject eventJsonOject = records.getJSONObject(s);
//				event.setConferenceId(getString(eventJsonOject, "conferenceId"));
//
//				if (eventJsonOject.has("data")) {
//					JSONObject data = eventJsonOject.getJSONObject("data");
//					event.setTitle(getString(data, "title"));
//					event.setStartdate(getString(data, "startdate"));
//					event.setEnddate(getString(data, "enddate"));
//					event.setConfernceBanner(getString(data, "ConfernceBanner"));
//					event.setConfLogo(getString(data, "ConfLogo"));
//					event.setDescription(getString(data, "description"));
//
//					if (data.has("cmeData")) {
//						JSONObject cmeData = data.getJSONObject("cmeData");
//						event.setCmeCredits(getString(cmeData, "cmeCredits"));
//						event.setCmeConferenceFee(getString(cmeData, "conferenceFee"));
//						event.setCmeEarlyFee(getString(cmeData, "earlyFee"));
//						event.setCmeEarlyLastData(getString(cmeData, "earlyLastData"));
//						event.setCmeAbstratData(getString(cmeData, "abstratData"));
//
//						if (cmeData.has("contactData")) {
//							JSONObject contactData = cmeData.getJSONObject("contactData");
//							event.setContactPhone(getString(contactData, "phone"));
//							event.setContactEmail(getString(contactData, "email"));
//						}
//					}
//
//					Venue venue = new Venue();
//
//					if (data.has("venue")) {
//						JSONObject venueObject = data.getJSONObject("venue");
//						venue.setVenue(getString(venueObject, "venue"));
//						venue.setAddress(getString(venueObject, "address"));
//						venue.setCity(getString(venueObject, "city"));
//						venue.setState(getString(venueObject, "state"));
//						venue.setCountry(getString(venueObject, "country"));
//						if (venueObject.has("0")) {
//							venueObject = venueObject.getJSONObject("0");
//							venue.setTicketName(getString(venueObject, "TicketName"));
//							venue.setDescription(getString(venueObject, "Description"));
//							venue.setTicketPrice(getString(venueObject, "TicketPrice"));
//							venue.setAvailableTickets(getString(venueObject, "AvailableTickets"));
//							venue.setSaleEnd(getString(venueObject, "SaleEnd"));
//							venue.setDiscount(getString(venueObject, "Discount"));
//						}
//						event.setVenue(venue);
//					}
//					if (data.has("Speakers")) {
//						ArrayList<Speaker> speakers = new ArrayList<>();
//						JSONArray speakersArray = data.getJSONArray("Speakers");
//						for (int j = 0; j < speakersArray.length(); j++) {
//							Speaker speaker = new Speaker();
//							JSONObject obj = speakersArray.getJSONObject(j);
//							speaker.setId(getString(obj, "id"));
//							speaker.setName(getString(obj, "name"));
//							speaker.setImagePath(getString(obj, "image_path"));
//							speaker.setCountryName(getString(obj, "country_name"));
//							speaker.setCountryFlag(getString(obj, "country_flag"));
//							speaker.setStateName(getString(obj, "state_name"));
//							speaker.setCityName(getString(obj, "city_name"));
//							speaker.setSpeciality(getString(obj, "speciality"));
//							speaker.setUrlStructure(getString(obj, "UrlRestructure"));
//							speakers.add(speaker);
//						}
//						event.setSpeakers(speakers);
//					}
//					if (data.has("attending")) {
//						JSONArray attendingArray = data.getJSONArray("attending");
//						ArrayList<Attendee> attendees = new ArrayList<>();
//
//						for (int j = 0; j < attendingArray.length(); j++) {
//							Attendee user = new Attendee();
//							JSONObject userInfo = attendingArray.getJSONObject(j);
//							user.setId(getString(userInfo, "id"));
//							user.setName(getString(userInfo, "name"));
//							user.setSpeker_image_path(getString(userInfo, "speker_image_path"));
//							user.setSpeker_id(getString(userInfo, "speker_id"));
//							user.setUrlRestructure(getString(userInfo, "UrlRestructure"));
//							user.setCountry_name(getString(userInfo, "country_name"));
//							user.setCountry_flag(getString(userInfo, "country_flag"));
//							user.setOrganizer_image_path(getString(userInfo, "organizer_image_path"));
//							user.setOrganizer_id(getString(userInfo, "organizer_id"));
//							user.setState_name(getString(userInfo, "state_name"));
//							user.setCity_name(getString(userInfo, "city_name"));
//							user.setUser_type(getString(userInfo, "user_type"));
//							user.setImage_path(getString(userInfo, "image_path"));
//							attendees.add(user);
//						}
//						event.setAttendingUsers(attendees);
//					}
//					if (data.has("tracking")) {
//						JSONArray attendingArray = data.getJSONArray("tracking");
//						ArrayList<Attendee> trackingUsers = new ArrayList<>();
//
//						for (int j = 0; j < attendingArray.length(); j++) {
//							Attendee user = new Attendee();
//							JSONObject userInfo = attendingArray.getJSONObject(j);
//							user.setId(getString(userInfo, "id"));
//							user.setName(getString(userInfo, "name"));
//							user.setSpeker_image_path(getString(userInfo, "speker_image_path"));
//							user.setSpeker_id(getString(userInfo, "speker_id"));
//							user.setUrlRestructure(getString(userInfo, "UrlRestructure"));
//							user.setCountry_name(getString(userInfo, "country_name"));
//							user.setCountry_flag(getString(userInfo, "country_flag"));
//							user.setOrganizer_image_path(getString(userInfo, "organizer_image_path"));
//							user.setOrganizer_id(getString(userInfo, "organizer_id"));
//							user.setState_name(getString(userInfo, "state_name"));
//							user.setCity_name(getString(userInfo, "city_name"));
//							user.setUser_type(getString(userInfo, "user_type"));
//							user.setImage_path(getString(userInfo, "image_path"));
//							trackingUsers.add(user);
//						}
//						event.setTrackingUsers(trackingUsers);
//					}
//					if (data.has("rating")) {
//						if (data.getJSONObject("rating").has("overallRating")) {
//							event.setTotalConferenceRating(getInt(data.getJSONObject("rating").getJSONObject("overallRating"), "totalConferenceRating"));
//						}
//					}
//					if (data.has("User")) {
//						event.setConfAttend(getInt(data.getJSONObject("User"), "confAttend"));
//						event.setConfTrack(getInt(data.getJSONObject("User"), "confTrack"));
//					}
//				}
//			}
//			return event;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return e;
//		}
//	}
//
//	private static Object parseGetTickets(String result) {
//		ArrayList<ShowTickets> tickets = new ArrayList<>();
//
//		try {
//			JSONObject reader = new JSONObject(result);
//			JSONObject records = (JSONObject) reader.get("record");
//
//			int scollstatedeside = 0;
//
//			// looping through All Contacts
//			for (int i = 0; i < records.length() - 1; i++) {
//				ShowTickets showTickets = new ShowTickets();
//
//				scollstatedeside = scollstatedeside + 1;
//				String s = Integer.toString(i);
//
//				JSONObject ticketJsonOject = records.getJSONObject(s);
//				showTickets.setTicketId(getString(ticketJsonOject, "ticketId"));
//				showTickets.setConferenceId(getString(ticketJsonOject, "conferemceId"));
//				showTickets.setTicketName(getString(ticketJsonOject, "TicketName"));
//				showTickets.setDescription(getString(ticketJsonOject, "description"));
//				showTickets.setPrice(getString(ticketJsonOject, "price"));
//				showTickets.setCurrency(getString(ticketJsonOject, "currency"));
//				showTickets.setCurrencySymbol(getString(ticketJsonOject, "currrency_symbol"));
//				showTickets.setTotalTickets(getString(ticketJsonOject, "totalTickets"));
//				showTickets.setSoldTickets(getString(ticketJsonOject, "soldTickets"));
//				if (ticketJsonOject.has("purchaseLimit")) {
//					JSONObject obj = ticketJsonOject.getJSONObject("purchaseLimit");
//					showTickets.setPurchaseMaxLimit(getString(obj, "maxLimit"));
//					showTickets.setPurchaseMinLimit(getString(obj, "minLimit"));
//				}
//				showTickets.setStartDate(getString(ticketJsonOject, "startDate"));
//				showTickets.setServiceTax(getString(ticketJsonOject, "serviceTax"));
//				showTickets.setServiceTaxPercentage(getString(ticketJsonOject, "serviceTaxPercent"));
//				showTickets.setStatus(getString(ticketJsonOject, "status"));
//				showTickets.setDiscount(getString(ticketJsonOject, "discount"));
//
//				tickets.add(showTickets);
//			}
//			return tickets;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return e;
//		}
//	}
//
//	private static Object parseUserProfile(String result) {
//		UserProfile profile = new UserProfile();
//		try {
//			JSONObject reader = new JSONObject(result);
//			if (reader.has("record")) {
//				JSONObject record = (JSONObject) reader.get("record");
//				profile.setImagePath(getString(record, "image_path"));
//				profile.setBriefBiography(getString(record, "brief_biography"));
//				profile.setUserId(getString(record, "userId"));
//				profile.setUserName(getString(record, "userName"));
//				profile.setEmail(getString(record, "email"));
//				profile.setFirstName(getString(record, "firstName"));
//				profile.setLastName(getString(record, "lastName"));
//				profile.setContactno(getString(record, "contactno"));
//				profile.setCityId(getString(record, "cityId"));
//				profile.setStateId(getString(record, "stateId"));
//				profile.setCountryId(getString(record, "countryId"));
//				profile.setCityName(getString(record, "City"));
//				profile.setStateName(getString(record, "State"));
//				profile.setCountryName(getString(record, "Country"));
//				profile.setProfilePercentage(getInt(record, "profilePercentage"));
//			}
//
//			return profile;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private static Object parseCities(String result) {
//		ArrayList<City> cities = new ArrayList<>();
//
//		try {
//			JSONObject reader = new JSONObject(result);
//			JSONArray recordsArray = (JSONArray) reader.get("record");
//			for (int i = 0; i < recordsArray.length(); i++) {
//				City city = new City();
//				JSONObject record = recordsArray.getJSONObject(i);
//				city.setId(getString(record, "cityId"));
//				city.setName(getString(record, "cityName"));
//				city.setLatitude(getString(record, "latitude"));
//				city.setLongitude(getString(record, "longitude"));
//				cities.add(city);
//			}
//			return cities;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private static Object parseStates(String result) {
//		ArrayList<State> states = new ArrayList<>();
//
//		try {
//			JSONObject reader = new JSONObject(result);
//			JSONArray recordsArray = (JSONArray) reader.get("record");
//			for (int i = 0; i < recordsArray.length(); i++) {
//				State state = new State();
//				JSONObject record = recordsArray.getJSONObject(i);
//				state.setId(getString(record, "stateId"));
//				state.setName(getString(record, "stateName"));
//				state.setCode(getString(record, "stateCode"));
//				state.setRegionType(getString(record, "regionType"));
//				states.add(state);
//			}
//			return states;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private static Object parseCountries(String result) {
//		ArrayList<Country> countries = new ArrayList<>();
//
//		try {
//			JSONObject reader = new JSONObject(result);
//			JSONArray recordsArray = (JSONArray) reader.get("record");
//			for (int i = 0; i < recordsArray.length(); i++) {
//				Country country = new Country();
//				JSONObject record = recordsArray.getJSONObject(i);
//				country.setId(getString(record, "countryId"));
//				country.setName(getString(record, "countryName"));
//				country.setCode(getString(record, "countryCode"));
//				country.setFlag(getString(record, "countryFlag"));
//				countries.add(country);
//			}
//			return countries;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//}
