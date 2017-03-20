/*
 * Utils.java 
 */

package com.lib;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Base64;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

public class Utils {

	/**
	 * Format phone number into (###) ###-#### format
	 * 
	 * @param number
	 *            a 10 digit phone number
	 * @return if number length is 10 then formatted number else the original
	 *         number
	 */
	public static String formatPhoneNumber(String number) {
		if (number.length() != 10)
			return number;
		else
			return "(" + number.substring(0, 3) + ") " + number.substring(3, 6) + "-" + number.substring(6, 10);
	}

	public static boolean isValidEmail(String email) {
		Pattern p = Pattern.compile("^[A-Za-z0-9](([_\\.\\-]?[a-zA-Z0-9]+)*)@([A-Za-z0-9]+)(([\\.\\-]?[a-zA-Z0-9]+)*)\\.([A-Za-z]{2,})$");
		Matcher m = p.matcher(email);
		return m.matches();
	}

	public static String getCurrentMethodName() {
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		return stackTraceElements[1].toString();
	}

	public static void unbindDrawables(View view) {
		if (view.getBackground() != null) {
			view.getBackground().setCallback(null);
		}
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				unbindDrawables(((ViewGroup) view).getChildAt(i));
			}
			((ViewGroup) view).removeAllViews();
		}
	}

	public static String bitmapToBase64String(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		String encoded = Base64.encodeToString(b, Base64.DEFAULT);
		return encoded;
	}

	public static String maskUserName(String userName) {
		final char MASK_CHAR = 'x';
		char[] maskedUname = userName.toCharArray();
		int maskLenght = maskedUname.length - 4;

		for (int i = 0; i < maskLenght; i++) {
			maskedUname[i] = MASK_CHAR;
		}

		return new String(maskedUname);
	}

	public static String getStringFromInputStream(InputStream stream) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	// convert String into InputStream
	public static InputStream getStreamFromString(String string) {
		return new ByteArrayInputStream(string.getBytes());
	}

	public static void makeMenuKeyDisable(Activity activity) {
		try {
			ViewConfiguration config = ViewConfiguration.get(activity);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}
	}

	/**
	 * get Month
	 * 
	 * @param substring
	 * @return
	 */
	public static String getMonth(String substring) {
		int month = Integer.parseInt(substring);
		switch (month) {
		case 1:
			return "January";
		case 2:
			return "February";
		case 3:
			return "March";
		case 4:
			return "April";
		case 5:
			return "May";
		case 6:
			return "June";
		case 7:
			return "July";
		case 8:
			return "August";
		case 9:
			return "September";
		case 10:
			return "October";
		case 11:
			return "November";
		case 12:
			return "December";
		default:
			break;
		}
		return null;
	}

	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
}
