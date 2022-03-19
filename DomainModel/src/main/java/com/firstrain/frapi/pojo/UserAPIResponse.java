package com.firstrain.frapi.pojo;

import com.firstrain.frapi.domain.User;

public class UserAPIResponse {

	private int statusCode;
	private User user;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
