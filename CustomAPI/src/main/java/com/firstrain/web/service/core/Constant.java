package com.firstrain.web.service.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Constant {

	private static Constant instance;

	// can be optional for demo components
	@Value("${user.activity.service.url:}")
	private String userActivityServiceURL;

	@Value("${app.name}")
	private String appName;

	@Value("${api.version}")
	private String version;

	private Constant() {
		instance = this;
	}

	public static String getUserActivityServiceURL() {
		return instance.userActivityServiceURL;
	}

	public static String getAppName() {
		return instance.appName;
	}

	public static String getVersion() {
		return instance.version;
	}
}
