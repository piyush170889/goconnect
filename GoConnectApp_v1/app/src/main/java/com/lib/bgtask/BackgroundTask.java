/*
 * BackgroundTask.java 
 */

package com.lib.bgtask;

import android.content.Context;
import android.os.AsyncTask;

import com.lib.PrintLog;

/**
 * The Class BackgroundTask.
 * 
 * @author rjonnalagadda
 */
public final class BackgroundTask extends AsyncTask<Void, Void, Object> {
	private static final String LOADING = "Loading...";

	private static final String TAG = "BackgroundTask";

	/** The Background Thread Object. */
	private BackgroundThread bgtask;

	/** The context. */
	private Context context;

	/** is task cancelled. */
	boolean isCancelled;

	private String loadingMessage;

	/**
	 * Instantiates a new background task.
	 * 
	 * @param context
	 *            the context
	 * @param bgtask
	 *            the Background Thread Object
	 */
	public BackgroundTask(Context context, BackgroundThread bgtask) {
		this.context = context;
		this.bgtask = bgtask;
		this.loadingMessage = LOADING;
	}

	public BackgroundTask(Context context, BackgroundThread bgtask, String loadingMessage) {
		this.context = context;
		this.bgtask = bgtask;
		this.loadingMessage = loadingMessage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPreExecute()
	 */
	@Override
	protected void onPreExecute() {
		PrintLog.print(TAG, "onPreExecute...");
		ProgressDialogBox.show(context, new ProcessCancelListener() {
			@Override
			public void processCanceled() {
				PrintLog.print(TAG, "processCanceled...");
				isCancelled = true;
				cancel(true);
			}
		}, loadingMessage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Object doInBackground(Void... params) {
		PrintLog.print(TAG, "doInBackground...");
		return bgtask.runTask();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Object result) {
		PrintLog.print(TAG, "onPostExecute...");
		if (!isCancelled) {
			ProgressDialogBox.close();
			bgtask.taskCompleted(result);
		}
	}

	@Override
	protected void onCancelled() {
		if (!isCancelled) {
			PrintLog.print(TAG, "onCancelled...");
			ProgressDialogBox.close();
			super.onCancelled();
		}
	}
}
