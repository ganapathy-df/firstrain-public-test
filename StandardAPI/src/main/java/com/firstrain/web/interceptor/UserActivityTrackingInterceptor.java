package com.firstrain.web.interceptor;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.web.pojo.UserActivity;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.UserActivityService;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import com.firstrain.web.util.UserInfoThreadLocal;

/**
 * @author vgoyal Interceptor for clicktracking, call rest service API :
 */
public class UserActivityTrackingInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOG = Logger.getLogger(UserActivityTrackingInterceptor.class);

	@Autowired
	private UserActivityService userActivityService;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse arg1, Object arg2) throws Exception {
		req.setAttribute("ST_TIME", System.nanoTime());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest req, HttpServletResponse arg1, Object arg2, ModelAndView arg3) throws Exception {
		try {
			if (!"true".equals(req.getParameter("track"))) {
				// when page loads if url contains ignorelogin=true then its won't be track.
				if ("true".equals(req.getParameter("ignorelogin"))) {
					return;
				}
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
							(req.getAttribute("target") != null ? req.getAttribute("target").toString() : null));
				}
				return;
			}
			int responseTime = (int) TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - (Long) req.getAttribute("ST_TIME"));
			userActivityService.addActivity(UserInfoThreadLocal.get(), AuthAPIResponseThreadLocal.get(),
					new HashMap<String, String[]>(req.getParameterMap()), new Timestamp(System.currentTimeMillis()), responseTime,
					req.getHeader("User-Agent"), (String) req.getAttribute("userIP"), getChannel(req), Constant.getVersion());
		} catch (Exception e) {
			LOG.error("Error while logging activity for: " + req.getRequestURI() + "?" + req.getQueryString(), e);
		}
	}

	private void loadViewActivity(HttpServletRequest req, String view, String targetId, String section, String metaData, String str1,
			String str2, String activityType, String viewId, String sectionId, String target) {
		UserActivity userActivity = new UserActivity();
		userActivity.setApiVersion(Constant.getVersion());
		User user = UserInfoThreadLocal.get();
		if (user != null && user.getUserId() != null) {
			userActivity.setUserId(Integer.valueOf(user.getUserId()));
		} else {
			userActivity.setUserId(9999);
		}
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

		AuthAPIResponse authAPIResponse = AuthAPIResponseThreadLocal.get();
		if (authAPIResponse != null) {
			int enterpriseId = (int) authAPIResponse.getEnterpriseId();
			userActivity.setStr2(authAPIResponse.getAuthKey());
			userActivity.setViewId(String.valueOf(enterpriseId));
			userActivity.setEnterpriseId(enterpriseId);
		}
		if (viewId != null && !viewId.isEmpty()) {
			userActivity.setViewId(viewId);
		}

		userActivity.setSection(section);
		userActivity.setSectionId(sectionId);
		userActivity.setActivityTime(new Timestamp(System.currentTimeMillis()));
		userActivity.setUserAgent(req.getHeader("User-Agent"));
		userActivity.setUserip((String) req.getAttribute("userIP"));
		userActivity.setMetaData(metaData);
		String str1Temp = str1;
		if (StringUtils.isEmpty(str1Temp)) {
			str1Temp = req.getHeader("Accept");
		}
		if (StringUtils.isNotEmpty(str1Temp)) {
			int lenChar = 110;
			if (str1Temp.length() > lenChar) {
				str1Temp = str1Temp.substring(0, lenChar);
			}
			userActivity.setStr1(str1Temp);
		}

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
		return Constant.getChannelName();
	}

}
