package com.firstrain.web.service.core;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Constant {

	public static final int FR_PAGINATION_COUNT = 300;
	public static final int TE_PAGINATION_COUNT = 300;
	public static final int E_PAGINATION_COUNT = 300;
	public static final int FT_PAGINATION_COUNT = 150;
	public static final int AC_PAGINATION_COUNT = 300;
	public static final int MULTI_SEARCH_PAGINATION_COUNT = 300;
	private static Constant instance;
	@Autowired
	private ServletContext servletContext;

	// can be optional for demo components
	@Value("${user.activity.service.url:}")
	private String userActivityServiceURL;

	@Value("${app.name}")
	private String appName;

	@Value("${channel.name}")
	private String channelName;

	@Value("${img.css.base.url}")
	private String imgCssURL;

	@Value("${js.base.url}")
	private String jsURL;

	@Value("${api.version}")
	private String version;

	@Value("${app.base.url}")
	private String appBaseUrl;

	private Constant() {
		instance = this;
	}

	@PostConstruct
	private void init() {
		servletContext.setAttribute("appName", instance.appName);
		servletContext.setAttribute("appBaseUrl", instance.appBaseUrl);
		servletContext.setAttribute("imgCssURL", instance.imgCssURL);
		servletContext.setAttribute("jsURL", instance.jsURL);
		servletContext.setAttribute("version", instance.version);
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

	public static String getChannelName() {
		return instance.channelName;
	}

	public static String getImgCssURL() {
		return instance.imgCssURL;
	}

	public static String getJsURL() {
		return instance.jsURL;
	}
}
