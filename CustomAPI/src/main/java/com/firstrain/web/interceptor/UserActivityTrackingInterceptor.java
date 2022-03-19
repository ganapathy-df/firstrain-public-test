package com.firstrain.web.interceptor;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.firstrain.frapi.customapirepository.impl.DocCategorizeRepositoryImpl;
import com.firstrain.web.pojo.UserActivity;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.UserActivityService;

/**
 * @author vgoyal Interceptor for clicktracking, call rest service API :
 */
public class UserActivityTrackingInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOG = Logger.getLogger(UserActivityTrackingInterceptor.class);

	@Autowired
	private UserActivityService userActivityService;

	@Autowired
	private DocCategorizeRepositoryImpl docCategorizeRepositoryImpl;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse arg1, Object arg2) throws Exception {
		req.setAttribute("ST_TIME", System.nanoTime());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {
		try {
			if (Boolean.TRUE.equals(req.getAttribute("loadview"))) {

				loadViewActivity(req, (req.getAttribute("view") != null ? req.getAttribute("view").toString() : "frapi"),
						(req.getAttribute("targetId") != null ? req.getAttribute("targetId").toString() : null),
						(req.getAttribute("section") != null ? req.getAttribute("section").toString() : null),
						(req.getAttribute("metaData") != null ? req.getAttribute("metaData").toString() : null),
						(req.getAttribute("str1") != null ? req.getAttribute("str1").toString() : null),
						(req.getAttribute("str2") != null ? req.getAttribute("str2").toString() : null),
						(req.getAttribute("activityType") != null ? req.getAttribute("activityType").toString() : null),
						(req.getAttribute("viewId") != null ? req.getAttribute("viewId").toString() : null),
						(req.getAttribute("sectionId") != null ? req.getAttribute("sectionId").toString() : null),
						(req.getAttribute("target") != null ? req.getAttribute("target").toString() : null),
						(req.getAttribute("enterpriseId") != null ? req.getAttribute("enterpriseId").toString() : null));
			}
		} catch (Exception e) {
			LOG.error("Error while logging activity for: " + req.getRequestURI() + "?" + req.getQueryString(), e);
		}
	}

	private void loadViewActivity(HttpServletRequest req, String view, String targetId, String section, String metaData, String str1,
			String str2, String activityType, String viewId, String sectionId, String target, String enterpriseId) {
		UserActivity userActivity = new UserActivity();
		userActivity.setApiVersion(Constant.getVersion());
		userActivity.setUserId(9999);
		userActivity.setChannel(getChannel(req));
		if (activityType != null) {
			userActivity.setActivity(activityType);
		} else {
			userActivity.setActivity("loadview");
		}
		userActivity.setView(view);
		// In loadview activity, targetId also be treated as viewId
		if (targetId != null && !targetId.isEmpty()) {
			// userActivity.setViewId(targetId);
			userActivity.setTargetId(targetId);
		} else {
			userActivity.setTarget("default");
		}
		if (target != null && !target.isEmpty()) {
			userActivity.setTarget(target);
		}
		/*
		 * if (viewId != null && !viewId.isEmpty()) { userActivity.setViewId(viewId); }
		 */
		/*
		 * AuthAPIResponse authAPIResponse = AuthAPIResponseThreadLocal.get(); if (authAPIResponse != null) {
		 * userActivity.setViewId(String.valueOf(authAPIResponse.getId())); }
		 */
		userActivity.setEnterpriseId(Integer.parseInt(enterpriseId));
		userActivity.setSection(section);
		userActivity.setSectionId(sectionId);
		userActivity.setActivityTime(new Timestamp(System.currentTimeMillis()));
		userActivity.setUserAgent(req.getHeader("User-Agent"));
		userActivity.setUserip((String) req.getAttribute("userIP"));
		userActivity.setMetaData(metaData);
		if (str1 != null) {
			userActivity.setStr1(str1);
		} else {
			userActivity.setStr1(req.getHeader("Accept"));
		}
		userActivity.setStr2(str2);
		int responseTime = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - (Long) req.getAttribute("ST_TIME"));
		userActivity.setResponseTime(responseTime);
		userActivityService.addActivity(userActivity);
	}

	private String getChannel(HttpServletRequest request) {
		String channel = request.getParameter("activityChannel");
		if (channel != null) {
			return channel;
		}
		channel = (String) request.getAttribute("activityChannel");
		if (channel != null) {
			return channel;
		}
		return Constant.getAppName();
	}

}
