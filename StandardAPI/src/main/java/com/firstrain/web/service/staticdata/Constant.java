package com.firstrain.web.service.staticdata;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Constant {

	@Autowired
	private ServletContext servletContext;
	private static Constant instance;

	@Value("${app.name}")
	private String appName;

	@Value("${img.css.base.url}")
	private String imgCssURL;

	@Value("${js.base.url}")
	private String jsURL;

	@Value("${api.version}")
	private String version;

	private Constant() {
		instance = this;
	}

	@PostConstruct
	private void init() {
		servletContext.setAttribute("appName", instance.appName);
		servletContext.setAttribute("imgCssURL", instance.imgCssURL);
		servletContext.setAttribute("jsURL", instance.jsURL);
		servletContext.setAttribute("version", instance.version);
	}

	public static String getAppName() {
		return instance.appName;
	}

	public static String getImgCssURL() {
		return instance.imgCssURL;
	}

	public static String getJsURL() {
		return instance.jsURL;
	}

	public static String getVersion() {
		return instance.version;
	}
}
