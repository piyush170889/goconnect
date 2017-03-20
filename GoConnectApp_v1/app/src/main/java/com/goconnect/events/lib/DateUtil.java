/**
 * 
 */
package com.goconnect.events.lib;

/**
 * @author Reddy
 * 
 */
public class DateUtil {

	public DateUtil() {
		// Public default constructor
	}

	public static String formatDate(String date) {
		int day = Integer.parseInt(date.substring(8, 10));
		int month = Integer.parseInt(date.substring(5, 7));
		int year = Integer.parseInt(date.substring(0, 4));

		return getMonth(month) + " " + day + ", " + year;
	}

	private static String getMonth(int month) {
		switch (month) {
		case 1:
			return "Jan";
		case 2:
			return "Feb";
		case 3:
			return "Mar";
		case 4:
			return "Apr";
		case 5:
			return "May";
		case 6:
			return "Jun";
		case 7:
			return "Jul";
		case 8:
			return "Aug";
		case 9:
			return "Sep";
		case 10:
			return "Oct";
		case 11:
			return "Nov";
		case 12:
			return "Dec";
		default:
			break;
		}
		return null;
	}

	public static String formatDateRange(String startDate, String endDate) {
		int startDay = Integer.parseInt(startDate.substring(8, 10));
		int startMonth = Integer.parseInt(startDate.substring(5, 7));
		int startYear = Integer.parseInt(startDate.substring(0, 4));
		int endDay = Integer.parseInt(endDate.substring(8, 10));
		int endMonth = Integer.parseInt(endDate.substring(5, 7));
		int endYear = Integer.parseInt(endDate.substring(0, 4));

		if (startYear != endYear) {
			return formatDate(startDate) + "-" + formatDate(endDate);
		} else if (startMonth != endMonth) {
			return getMonth(startMonth) + " " + startDay + " - " + getMonth(endMonth) + " " + endDay + ", " + startYear;
		} else if (startDay != endDay) {
			return getMonth(startMonth) + " " + startDay + " - " + endDay + ", " + startYear;
		} else {
			return formatDate(startDate);
		}
	}
}
