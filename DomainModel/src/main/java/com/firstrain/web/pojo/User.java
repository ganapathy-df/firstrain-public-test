package com.firstrain.web.pojo;

/**
 * @author vgoyal
 *
 */

public class User {

	private String userId;
	private String uStatus;
	private String uEmail;
	private String uFirstName;
	private String uLastName;
	private String uTimezone;
	private String uCompany;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getuStatus() {
		return uStatus;
	}

	public void setuStatus(String uStatus) {
		this.uStatus = uStatus;
	}

	public String getuEmail() {
		return uEmail;
	}

	public void setuEmail(String uEmail) {
		this.uEmail = uEmail;
	}

	public String getuFirstName() {
		return uFirstName;
	}

	public void setuFirstName(String uFirstName) {
		this.uFirstName = uFirstName;
	}

	public String getuLastName() {
		return uLastName;
	}

	public void setuLastName(String uLastName) {
		this.uLastName = uLastName;
	}

	public String getuTimezone() {
		return uTimezone;
	}

	public void setuTimezone(String uTimezone) {
		this.uTimezone = uTimezone;
	}

	public String getuCompany() {
		return uCompany;
	}

	public void setuCompany(String uCompany) {
		this.uCompany = uCompany;
	}
}
