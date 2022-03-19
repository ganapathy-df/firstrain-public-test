package com.firstrain.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.firstrain.web.service.core.EnterpriseConfigLoader;
import com.firstrain.web.service.core.IPValidatorService;
import com.firstrain.web.util.EnterpriseConfigThreadLocal;
import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;

public class AuthInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = Logger.getLogger(AuthInterceptor.class);
	@Autowired
	private EnterpriseConfigLoader enterpriseConfigLoader;
	@Autowired
	private IPValidatorService ipValidatorService;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object arg2) throws Exception {
		EnterpriseConfigThreadLocal.remove();

		boolean validReq = true;
		boolean validIp = true;
		EnterpriseConfig ec = null;

		String token = req.getHeader("authKey");
		if (token != null) {
			ec = enterpriseConfigLoader.getEnterpriseConfig(token);
			if (ec == null) {
				validReq = false;
				LOG.warn("Invalid authKey passed in the req header, authKey: " + token);
			} else {
				req.setAttribute("enterpriseId", ec.getId());
				validIp = ipValidatorService.isIPSecure(req, ec.getId());
			}
		} else {
			validReq = false;
			LOG.warn("authKey not passed in the req header");
		}

		if (!validReq || !validIp) {
			try {
				if (!validIp) {
					req.setAttribute("invalidIP", true);
					res.setStatus(403);
				} else if (!validReq) {
					req.setAttribute("unauthorized", true);
					res.setStatus(401);
				}
			} catch (Exception e) {
			}
			req.getRequestDispatcher("/WEB-INF/pages/errorMessage.jsp").forward(req, res);
			return false;
		}

		EnterpriseConfigThreadLocal.set(ec);

		return true;
	}
}
