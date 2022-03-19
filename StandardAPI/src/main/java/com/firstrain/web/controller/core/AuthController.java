/**
 * 
 */
package com.firstrain.web.controller.core;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.frapi.service.AuthService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.response.AuthKeyResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.AuthKeyCacheManager;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.AuthAPIResponseThreadLocal;

/**
 * @author gkhanchi
 *
 */
@Controller
public class AuthController {
	private static final Logger LOG = Logger.getLogger(AuthController.class);

	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private AuthKeyCacheManager authKeyCacheManager;
	@Autowired
	private AuthService authService;

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value = "/admin/generateAuthKey", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse generateAuthKey(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody) {
		AuthKeyResponse res = null;
		int errorCode = -1;
		try {
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "authorization");

			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}
			Map<String, String> reqParam = null;
			String userName = null;
			String password = null;
			try {
				reqParam = JSONUtility.deserialize(reqBody, Map.class);
				userName = requestParsingService.getRefinedReqVal(reqParam.get("authName"));
				password = requestParsingService.getRefinedReqVal(reqParam.get("authPassword"));
				request.setAttribute("metaData", "{\"authName\":\"" + userName + "\"}");
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}

			if (reqParam == null || userName == null || password == null) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("insufficient arguments");
			}
			AuthAPIResponse apiRes = authService.generateAuthKey(userName, password, Constant.getVersion());
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in generateAuthKey api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				res = responseDecoratorService.getAuthKeyResponse(apiRes);
				AuthAPIResponseThreadLocal.set(apiRes);
				// update cache map
				authKeyCacheManager.setAuthKey(apiRes);
			} else {
				throw new Exception("generateAuthKey api returned null.");
			}

		} catch (Exception e) {
			if (errorCode > 0) {
				LOG.warn(e.getMessage());
			} else {
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return res;
	}

	@SuppressWarnings({"rawtypes"})
	@RequestMapping(value = "/admin/getAuthKey/{key}", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse getAuthKey(HttpServletRequest request, HttpServletResponse response, @PathVariable("key") String authKey) {
		AuthKeyResponse res = null;
		int errorCode = -1;
		try {
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "authorization");

			if (authKey == null || authKey.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}

			AuthAPIResponse apiRes = authService.getAuthKeyDetails(authKey);
			if (apiRes != null && apiRes.getStatusCode() != 200 && apiRes.getStatusCode() != 116) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in getAuthKeyDetails api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				res = responseDecoratorService.getAuthKeyResponse(apiRes);
			} else {
				throw new Exception("getAuthKeyDetails api returned null.");
			}
		} catch (Exception e) {
			if (errorCode > 0) {
				LOG.warn(e.getMessage());
			} else {
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return res;
	}
}
