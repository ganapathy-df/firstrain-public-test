package com.firstrain.web.util;

import com.firstrain.frapi.domain.User;


public class UserInfoThreadLocal {

	private static final ThreadLocal<User> userInfoThreadLocal = new ThreadLocal<User>();

	public static void set(User userInfo) {
		userInfoThreadLocal.set(userInfo);
	}

	public static User get() {
		return userInfoThreadLocal.get();
	}

	public static void remove() {
		userInfoThreadLocal.remove();
	}

}
