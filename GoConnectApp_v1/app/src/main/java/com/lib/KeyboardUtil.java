/*
 * KeyboardUtil.java 
 */

package com.lib;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.devjiva.goconnect.BuildConfig;

/**
 * The Class KeyboardUtil for Android Soft Keyboard utilities
 *
 * @author Reddy
 */
public class KeyboardUtil {

    private static final String TAG = "KeyboardUtil";

    /**
     * Hide the Soft Keyboard.
     *
     * @param activity the activity
     */
    public static void hideKeyboard(Activity activity) {
        try {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }


        } catch (Exception e) {
            // Ignore exceptions if any
            if (BuildConfig.DEBUG)
                Log.e(TAG, e.toString(), e);
        }
    }
}
