package com.firstrain.web.util;

import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;


public class EnterpriseConfigThreadLocal {

	private static final ThreadLocal<EnterpriseConfig> enterpriseConfigThreadLocal = new ThreadLocal<EnterpriseConfig>();

	public static void set(EnterpriseConfig authAPIResponse) {
		enterpriseConfigThreadLocal.set(authAPIResponse);
	}

	public static EnterpriseConfig get() {
		return enterpriseConfigThreadLocal.get();
	}

	public static void remove() {
		enterpriseConfigThreadLocal.remove();
	}

}
