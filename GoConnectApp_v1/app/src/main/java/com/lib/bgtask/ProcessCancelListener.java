/*
 * ProcessCancelListener.java 
 *
 */

package com.lib.bgtask;

/**
 * The listener interface for receiving processCancel events. The class that is
 * interested in processing a processCancel event implements this interface.
 * Register this listener when some background data loading/processing is going
 * on and you want to track the cancel event
 */
public interface ProcessCancelListener {

	/**
	 * Process canceled.
	 */
	void processCanceled();
}
