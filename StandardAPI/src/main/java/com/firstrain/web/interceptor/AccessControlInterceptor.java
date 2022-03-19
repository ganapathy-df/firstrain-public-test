package com.firstrain.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * @author gkhanchi Interceptor for HTTP access control - using Cross Origin Resource Sharing(CORS)
 */
public class AccessControlInterceptor extends HandlerInterceptorAdapter {
	private static final Logger LOG = Logger.getLogger(AccessControlInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object arg2) throws Exception {
		String origin = req.getHeader("Origin");
		if (origin != null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Origin in the request: " + origin);
			}
			res.addHeader("Access-Control-Allow-Origin", origin);
			res.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST");
			res.addHeader("Access-Control-Allow-Headers", "authKey,frUserId");
		}
		if (req.getMethod().equals("OPTIONS")) {
			return false;
		}
		return true;
	}
}
