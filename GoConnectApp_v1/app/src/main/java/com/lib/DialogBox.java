/*
 * DialogBox.java 
 */

package com.lib;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.devjiva.goconnect.R;

/**
 * The Class DialogBox.
 * 
 * @author Reddy
 */
public class DialogBox extends DialogFragment {

	public static final String TAG_DIALOG = "dialog";

	private static final String KEY_MESSAGE = "message";

	private String mPositiveButtonText;
	private OnClickListener mPositiveButtonListener;
	private String mNegativeButtonText;
	private OnClickListener mNegativeButtonListener;
	private String mNeutralButtonText;
	private OnClickListener mNeutralButtonListener;

	// private final int CANCEL_DIALOG = 1;
	// private Handler mHandler;
	private Dialog mDialog;

	private String mTitle = null;

	/**
	 * Instantiates a new dialog box.
	 */
	public DialogBox() {
	}

	/**
	 * New instance.
	 * 
	 * @param title
	 *            the title
	 * @return the dialog box
	 */
	public static DialogBox newInstance(String title) {
		DialogBox frag = new DialogBox();
		Bundle args = new Bundle();
		args.putString(KEY_MESSAGE, title);
		frag.setArguments(args);
		frag.setRetainInstance(true);
		frag.setCancelable(false);
		return frag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String message = getArguments().getString(KEY_MESSAGE);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		if (mTitle != null) {
			builder.setTitle(mTitle).setMessage(message);
		} else {
			builder.setTitle(R.string.app_name).setMessage(message);
		}

		// If no custom listener has been set then set the default OK button
		if (mPositiveButtonListener == null && mNegativeButtonListener == null && mNeutralButtonListener == null) {
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
				}
			});
		}

		// If listeners available then set those
		if (mPositiveButtonListener != null) {
			builder.setPositiveButton(mPositiveButtonText, mPositiveButtonListener);
		}

		if (mNegativeButtonListener != null) {
			builder.setNegativeButton(mNegativeButtonText, mNegativeButtonListener);
		}

		if (mNeutralButtonListener != null) {
			builder.setNeutralButton(mNeutralButtonText, mNeutralButtonListener);
		}

		// Create the AlertDialog object and return it
		mDialog = builder.create();
		// if ( (Globals.IS_SESSION_EXPIRED) && (!Globals.EXPLICIT_LOG_OUT) )
		// mHandler.sendEmptyMessageDelayed(CANCEL_DIALOG, 10000);
		return mDialog;
	}

	/**
	 * Sets the positive button text and listener.
	 * 
	 * @param buttonText
	 *            the button text
	 * @param listener
	 *            the listener
	 */
	public void setPositiveButton(String buttonText, OnClickListener listener) {
		mPositiveButtonText = buttonText;
		mPositiveButtonListener = listener;
	}

	/**
	 * Sets the negative button text and listener.
	 * 
	 * @param buttonText
	 *            the button text
	 * @param listener
	 *            the listener
	 */
	public void setNegativeButton(String buttonText, OnClickListener listener) {
		mNegativeButtonText = buttonText;
		mNegativeButtonListener = listener;
	}

	/**
	 * Sets the neutral button text and listener.
	 * 
	 * @param buttonText
	 *            the button text
	 * @param listener
	 *            the listener
	 */
	public void setNeutralButton(String buttonText, OnClickListener listener) {
		mNeutralButtonText = buttonText;
		mNeutralButtonListener = listener;
	}

	public void setTitle(String title) {
		mTitle = title;
	}
}
