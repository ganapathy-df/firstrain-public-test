package com.firstrain.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AuthInterceptorStaticAPI extends HandlerInterceptorAdapter {

	private static final Logger LOG = Logger.getLogger(AuthInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object arg2) throws Exception {

		if (req.getRequestURI().endsWith("generateAuthKey") || req.getMethod().equals("OPTIONS")) {
			return true;
		}
		boolean validReq = true;
		String token = req.getHeader("authKey");
		if (token == null && (req.getHeader("Accept") != null && req.getHeader("Accept").contains("text/html")
				|| "true".equals(req.getParameter("authInReqParam")))) {
			token = req.getParameter("authKey");
		}
		if (token == null || !token.equals("Smi8E2GgBS16cl9w2q4aEIu0")) {
			validReq = false;
			LOG.warn("Invalid token passed in the URL, token: " + token);
		}

		String frUserId = req.getHeader("frUserId");
		if (frUserId == null && (req.getHeader("Accept") != null && req.getHeader("Accept").contains("text/html")
				|| "true".equals(req.getParameter("authInReqParam")))) {
			frUserId = req.getParameter("frUserId");
		}
		if (frUserId == null) {
			validReq = false;
			LOG.warn("Header frUserId not passed in the URL");
		}

		if (!validReq) {
			req.setAttribute("unauthorized", true);
			try {
				res.setStatus(401);
			} catch (Exception e) {
			}
			req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req, res);
			return false;
		}

		return true;
	}
}
