/*
 * BackgroundThread.java 
 */

package com.lib.bgtask;

/**
 * The Interface BackgroundThread. Implement this if you want to run any task in
 * a background thread.
 * 
 * @author rjonnalagadda
 */
public interface BackgroundThread {

	/**
	 * Run task. The implemented code will run in the background and any result
	 * will be returned to {@link #taskCompleted(Object)}
	 * 
	 * @return the object
	 */
	Object runTask();

	/**
	 * Task completed. The implemented code will run in the foreground using the
	 * data returned by the {@link #runTask()}
	 * 
	 * @param data
	 *            the data
	 */
	void taskCompleted(Object data);
}
