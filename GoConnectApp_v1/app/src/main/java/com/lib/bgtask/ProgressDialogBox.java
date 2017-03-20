/*
 * ProgressDialogBox.java
 */

package com.lib.bgtask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;

/**
 * The Class ProgressDialogBox is used to show Progress Dialog.
 */
public final class ProgressDialogBox {

	private static final String TAG = "ProgressDialogBox";
	private static final String LOADING = "Loading...";
	/** The progress Dialog */
	private static ProgressDialog progress;

	/**
	 * Instantiates a new progress dialog box.
	 */
	private ProgressDialogBox() {
		// Singleton class
	}

	public static void show(final Context context) {
		show(context, null);
	}

	public static void show(final Context context, final ProcessCancelListener processCancelListener) {
		show(context, processCancelListener, LOADING);
	}

	/**
	 * Show the Progress Dialog.
	 * 
	 * @param context
	 *            the context
	 * @param processCancelListener
	 *            the process cancel listener
	 */
	public static void show(final Context context, final ProcessCancelListener processCancelListener, String message) {
		close();
		progress = new ProgressDialog(context);
		// progress.setTitle("Loading.");
		progress.setMessage(message);
		progress.setIndeterminate(true);
		progress.setCancelable(false);

		if (processCancelListener != null) {
			progress.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					processCancelListener.processCanceled();
				}
			});
		}

		if (progress == null)
			return;
		synchronized (progress) {
			try {
				progress.show();
			} catch (Exception e) {
				Log.e(TAG, e.toString());
				// e.printStackTrace();
			}
		}
	}

	/**
	 * Close the Progress Dialog.
	 */
	public static void close() {
		try {
			if (progress != null) {
				synchronized (progress) {
					progress.dismiss();
					progress = null;
				}
			}
		} catch (Exception e) // To handle device orientation change crash
		{
			Log.e(TAG, e.toString(), e);
		}
	}
}
