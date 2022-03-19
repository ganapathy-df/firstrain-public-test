package com.firstrain.web.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.utils.JSONUtility;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;

@Controller
public class BaseWidgetController {

	private static final Logger logger = Logger.getLogger(BaseWidgetController.class);
	
	@Value("${img.css.base.url}")
	private String imgCssURL;
	@Value("${js.base.url}")
	private String jsURL;
	@Value("${app.name}")
	private String appName;
	@Value("${api.version}")
	private String version;

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public void getConfig(HttpServletResponse response, @RequestParam("callback") String callback) {
		response.setContentType("text/javascript; charset=UTF-8");
		JSONResponse<Map<String, String>> res = new JSONResponse<Map<String, String>>();
		try {
			Map<String, String> config = new HashMap<String, String>();
			config.put("imgCssURL", imgCssURL);
			config.put("jsURL", jsURL);
			config.put("version", version);
			config.put("appName", appName);

			res.setResult(config);
			res.setStatus(ResStatus.SUCCESS);
		} catch (Exception e) {
			res.setStatus(ResStatus.ERROR);
			logger.error("Error while setting config data", e);
		}
		try {
			PrintWriter out = response.getWriter();
			out.print(callback + "(" + JSONUtility.serialize(res) + ")");
		} catch (Exception e) {
			logger.error("Error while sending config response", e);
		}
	}
	
	@RequestMapping(method = RequestMethod.OPTIONS, value = "/*")
	public void manageOptions(HttpServletRequest req, HttpServletResponse res) {
		String origin = req.getHeader("Origin");
		res.addHeader("Access-Control-Allow-Origin", origin);
		res.addHeader("Access-Control-Allow-Methods", "GET");
		res.addHeader("Access-Control-Allow-Headers", "authKey,frUserId");
	}
}
