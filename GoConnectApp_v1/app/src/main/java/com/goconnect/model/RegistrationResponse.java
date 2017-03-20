/**
 * 
 */
package com.goconnect.model;

import java.io.Serializable;

/**
 * @author Rama
 *
 */
public class RegistrationResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String status;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
