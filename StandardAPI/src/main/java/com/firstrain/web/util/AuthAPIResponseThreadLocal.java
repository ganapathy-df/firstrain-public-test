package com.firstrain.web.util;

import com.firstrain.frapi.pojo.AuthAPIResponse;


public class AuthAPIResponseThreadLocal {

	private static final ThreadLocal<AuthAPIResponse> authAPIResponseThreadLocal = new ThreadLocal<AuthAPIResponse>();

	public static void set(AuthAPIResponse authAPIResponse) {
		authAPIResponseThreadLocal.set(authAPIResponse);
	}

	public static AuthAPIResponse get() {
		return authAPIResponseThreadLocal.get();
	}

	public static void remove() {
		authAPIResponseThreadLocal.remove();
	}

}
