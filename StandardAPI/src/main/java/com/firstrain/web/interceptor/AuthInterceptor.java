package com.firstrain.web.interceptor;

import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.service.AuthService;
import com.firstrain.frapi.service.UserService;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.DefaultEnums.MembershipType;
import com.firstrain.web.service.core.AuthKeyCacheManager;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;
import com.firstrain.web.util.UserInfoThreadLocal;

public class AuthInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOG = Logger.getLogger(AuthInterceptor.class);
	@Autowired
	private AuthKeyCacheManager authKeyCacheManager;
	@Autowired
	private AuthService authService;
	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object arg2) throws Exception {

		UserInfoThreadLocal.remove();
		AuthAPIResponseThreadLocal.remove();

		if (req.getRequestURI().endsWith("generateAuthKey") || req.getMethod().equals("OPTIONS")) {
			return true;
		}
		boolean validReq = true;
		// handling for getAuthKey api - user should be active admin
		if (req.getRequestURI().contains("/admin/getAuthKey/")) {
			String frUserId = req.getHeader("frUserId");
			if (frUserId == null || !isValidUserWithoutDomainCheck(frUserId, true)) {
				validReq = false;
				LOG.warn("Header frUserId is not valid: " + frUserId);
			}
		} else {
			String token = req.getHeader("authKey");
			if (token == null && (req.getHeader("Accept") != null && !req.getHeader("Accept").contains("application/json")
					|| "true".equals(req.getParameter("authInReqParam")))) {
				token = req.getParameter("authKey");
			}
			// validate token
			if (token == null || !isValidToken(token)) {
				validReq = false;
				LOG.warn("Invalid token passed in the URL, token: " + token);
			}

			if (validReq) {
				AuthAPIResponse authResp = AuthAPIResponseThreadLocal.get();

				// validate version for enterprise
				if (!Constant.getVersion().equalsIgnoreCase(authResp.getApiVersion())) {
					validReq = false;
					req.setAttribute("invalidVersion", true);
					LOG.warn("Version: " + Constant.getVersion() + " not supported for enterprise: " + authResp.getEnterpriseId());
				}
				// validate excluded apis for enterprise
				String api = req.getRequestURI().substring(req.getRequestURI().lastIndexOf("/") + 1);
				if (authResp.getExcludedAPIList() != null && authResp.getExcludedAPIList().contains(api)) {
					validReq = false;
					req.setAttribute("invalidAPI", true);
					LOG.warn("API : " + api + " not supported for enterprise: " + authResp.getEnterpriseId());
				}
			}

			// validate user
			if (validReq) {
				String frUserId = req.getHeader("frUserId");
				if (frUserId == null && (req.getHeader("Accept") != null && !req.getHeader("Accept").contains("application/json")
						|| "true".equals(req.getParameter("authInReqParam")))) {
					frUserId = req.getParameter("frUserId");
				}

				// only admin user can access these apis
				if (req.getRequestURI().endsWith("barMe") || req.getRequestURI().endsWith("add")) {
					if (frUserId == null || !isValidUser(frUserId, true)) {
						validReq = false;
						LOG.warn("Header/Param frUserId is not valid or not ACTIVE ADMIN: " + frUserId);
					}
				} else if (frUserId == null || !isValidUser(frUserId)) {
					validReq = false;
					LOG.warn("Header/Param frUserId is not valid: " + frUserId);
				}
			}
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

	private boolean isValidUser(String userIdParam) {
		String userId = userIdParam;
		boolean isValid = false;
		try {
			userId = userId.split(":")[1]; // U:113
			UserAPIResponse apiRes = userService.getUserById(Long.parseLong(userId), true);
			if (apiRes != null && apiRes.getStatusCode() == 200 && apiRes.getUser() != null
					&& apiRes.getUser().getFlags().equalsIgnoreCase(DefaultEnums.Status.ACTIVE.name())) {
				isValid = checkIsValidUser(apiRes, userId, isValid); 
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.warn("Invalid userId: " + userId);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return isValid;
	}

	private boolean isValidUser(String userIdParam, boolean checkIfAdmin) {
		String userId = userIdParam;
		boolean isValid = false;
		try {
			userId = userId.split(":")[1]; // U:113
			UserAPIResponse apiRes = userService.getUserById(Long.parseLong(userId), checkIfAdmin);
			if (apiRes != null && apiRes.getStatusCode() == 200 && apiRes.getUser() != null
					&& apiRes.getUser().getFlags().equalsIgnoreCase(DefaultEnums.Status.ACTIVE.name())
					&& apiRes.getUser().getMembershipType().equals(MembershipType.ADMIN)) {
				isValid = checkIsValidUser(apiRes, userId, isValid); 
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.warn("Invalid userId: " + userId);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return isValid;
	}
 
	private boolean checkIsValidUser(final UserAPIResponse apiRes, final String userId, boolean isValidParam) { 
		boolean isValid = isValidParam;
		long enterpriseId = AuthAPIResponseThreadLocal.get().getEnterpriseId(); 
		if (enterpriseId != apiRes.getUser().getOwnedBy()) { 
			LOG.warn("UserId: " + userId + ", enterpriseId: " + apiRes.getUser().getOwnedBy() 
					+ " doesn't belongs to the enterprise of authkey, enterpriseId " + enterpriseId); 
		} else { 
			UserInfoThreadLocal.set(apiRes.getUser()); 
			isValid = true; 
		} 
		return isValid; 
	} 

	private boolean isValidUserWithoutDomainCheck(String userIdParam, boolean checkIfAdmin) {
		String userId = userIdParam;
		boolean isValid = false;
		try {
			userId = userId.split(":")[1]; // U:113
			UserAPIResponse apiRes = userService.getUserById(Long.parseLong(userId), checkIfAdmin);
			if (apiRes != null && apiRes.getStatusCode() == 200 && apiRes.getUser() != null
					&& apiRes.getUser().getFlags().equalsIgnoreCase(DefaultEnums.Status.ACTIVE.name())
					&& apiRes.getUser().getMembershipType().equals(MembershipType.ADMIN)) {
				UserInfoThreadLocal.set(apiRes.getUser());
				isValid = true;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			LOG.warn("Invalid userId: " + userId);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return isValid;
	}

	private boolean isValidToken(String key) {
		boolean isValid = false;
		try {
			// first retreive from cache
			AuthAPIResponse authAPIResponse = authKeyCacheManager.getAuthKey(key);
			// if not found in cache - fetch from db
			if (authAPIResponse == null) {
				LOG.debug("auth key not found in cache, fetching from db.");
				authAPIResponse = authService.getAuthKeyDetails(key);
			}
			if (authAPIResponse != null && authAPIResponse.getExpiryTime() != null) {
				Timestamp currTime = new Timestamp(System.currentTimeMillis());
				if (currTime.before(authAPIResponse.getExpiryTime()) || currTime.equals(authAPIResponse.getExpiryTime())) {
					isValid = true;
					// update the cache
					authKeyCacheManager.setAuthKey(authAPIResponse);
				}
				AuthAPIResponseThreadLocal.set(authAPIResponse);
			}
			LOG.info("auth key isValid: " + isValid);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return isValid;
	}
}
