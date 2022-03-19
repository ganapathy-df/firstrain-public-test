/**
 * A generic Interceptor that can be attached to any url for standardized logging to the webapp performance logs.
 */
package com.firstrain.web.interceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.utils.object.PerfRequestEntry;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.util.PerfRequestEntryScanObserver;

public class PerfLoggingInterceptor extends HandlerInterceptorAdapter {

	private static final Logger PERF_LOG = Logger.getLogger("PerfLogging");
	private static final String TRACKING_HEADER = "frtrck";
	private static final AtomicLong NUM = new AtomicLong(System.currentTimeMillis());
	private String appId = null;

	static {
		PerfRequestEntryScanObserver observer = new PerfRequestEntryScanObserver();
		observer.setAlertTime(2 * 1000);
		PerfMonitor.registerObserver(observer);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		boolean hasException = false;
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			String userIP = getRemoteHost(request);
			String req = userIP + ";" + request.getRequestURI() + "?" + request.getQueryString();
			request.setAttribute("userIP", userIP);
			String dynAppId = getDynamicAppId();
			request.setAttribute("appId", dynAppId);
			request.setAttribute("serverStartTime", System.currentTimeMillis());
			PerfMonitor.startRequest("-1", req, dynAppId);
		} catch (Exception e) {
			hasException = true;
			throw e;
		} finally {
			if (hasException) {
				PerfRequestEntry stat = PerfMonitor.endRequest();
				String statDetails = stat.toStringLog();
				request.setAttribute("perfstat", hasException + request.getRequestURI() + statDetails);
				PERF_LOG.info("REQERR: " + statDetails);
				if (PERF_LOG.isDebugEnabled()) {
					PERF_LOG.debug(stat.toString());
				}
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
			throws Exception {
		boolean hasException = false;
		try {
			pageLoadLogging(request);
			response.setHeader(TRACKING_HEADER, getTrackingInfo(request));
		} catch (Exception e) {
			hasException = true;
			throw e;
		} finally {
			PerfRequestEntry stat = PerfMonitor.endRequest();
			String statDetails = stat.toStringLog();
			if (hasException) {
				request.setAttribute("perfstat", hasException + request.getRequestURI() + statDetails);
				PERF_LOG.info("REQERR: " + statDetails);
			} else {
				PERF_LOG.info(statDetails);
			}
			if (PERF_LOG.isDebugEnabled()) {
				PERF_LOG.debug(stat.toString());
			}
		}
	}

	private String getTrackingInfo(HttpServletRequest request) {
		return getRemoteHost(request);
	}

	private void pageLoadLogging(HttpServletRequest hreq) {
		try {
			String clientStartTime = hreq.getParameter("clientStartTime");
			if (clientStartTime != null) {
				// Thu Jun 12 2014 19:52:35 GMT-0700 (Pacific Standard Time)
				long clientStart = new SimpleDateFormat("EEE MMM d yyyy HH:mm:ss 'GMT'Z").parse(clientStartTime).getTime();
				long serverStart = Long.parseLong(hreq.getParameter("serverStartTime"));

				PERF_LOG.info(String.format("Page wait time for appId:%s; page:%s; waitTime:%s; renderTime:%s", hreq.getParameter("appId"),
						hreq.getParameter("page"), (clientStart - serverStart), hreq.getParameter("renderTime")));
			}
		} catch (Exception e) {
		}
	}

	private String getRemoteHost(HttpServletRequest request) {
		String remoteHost = request.getHeader("X-Forwarded-For");
		if (remoteHost == null) {
			remoteHost = request.getHeader("X-Forward-For");
		}
		/* This is placed here to avoid exceptions during local tests. */
		if (remoteHost == null) {
			remoteHost = request.getRemoteAddr();
		}
		return remoteHost;
	}

	private String getDynamicAppId() {
		if (appId == null) {
			try {
				InetAddress ipAddress = InetAddress.getLocalHost();
				appId = ipAddress.getHostAddress() + "@" + Constant.getAppName();
			} catch (UnknownHostException e) {
				appId = "unknownhost" + "@" + Constant.getAppName();
			}
		}
		return appId + "-" + NUM.getAndIncrement();
	}
}
