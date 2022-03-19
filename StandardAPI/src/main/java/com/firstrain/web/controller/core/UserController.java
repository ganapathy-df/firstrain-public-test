/**
 * 
 */
package com.firstrain.web.controller.core;

import java.util.Map;

import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.UserAPIResponse;
import com.firstrain.frapi.service.MonitorService;
import com.firstrain.frapi.service.UserService;
import com.firstrain.frapi.util.DefaultEnums;
import com.firstrain.frapi.util.DefaultEnums.MembershipType;
import com.firstrain.frapi.util.DefaultEnums.OwnedByType;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.FR_MailUtils;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.MonitorDetailsResponse;
import com.firstrain.web.response.UserIdWrapperResponse;
import com.firstrain.web.response.UserWrapperResponse;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.UserInfoThreadLocal;

/**
 * @author vgoyal
 *
 */
@Controller
@RequestMapping(value = "/user")
public class UserController {
	private static final Logger LOG = Logger.getLogger(UserController.class);

	@Autowired
	private UserService userService;
	@Autowired
	private MonitorService monitorService;
	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Value("${timezone.supported}")
	private String timezones;

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{userId}/get", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse get(HttpServletRequest request, HttpServletResponse response, @PathVariable("userId") String userIdStr) {
		UserWrapperResponse res = null;
		int errorCode = -1;
		UserAPIResponse apiRes = null;
		try {
			User actor = UserInfoThreadLocal.get();
			long fruserId = -1;
			boolean isValidEmail = validateEmail(userIdStr);
			if (isValidEmail) {
				apiRes = userService.getUserByEmail(userIdStr, actor.getOwnedBy());
				fruserId = retrieveFrUserId(apiRes, fruserId); 
				
			} else {
				try {
					String userId = userIdStr.split(":")[1]; // U:113
					fruserId = Long.parseLong(userId);
				} catch (Exception e) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					throw e;
				}
				apiRes = userService.getUserById(fruserId);
			}

			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in getUserById api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				// allow only if both actor and user are same or actor should be active admin and of same account
				if (fruserId == Long.parseLong(actor.getUserId()) || isValidActor(actor, apiRes.getUser())) {
					res = responseDecoratorService.getUserResponse(apiRes, "gen.succ");
				} else {
					errorCode = StatusCode.INSUFFICIENT_PRIVILEGE;
					throw new Exception("actor should be self or ACTIVE ADMIN and of same account.");
				}
			} else {
				throw new Exception("getUserById api returned null.");
			}
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "getuser");
			request.setAttribute("targetId", userIdStr);
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value = "/add", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse add(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody) {
		UserWrapperResponse res = null;
		int errorCode = -1;

		Map<String, String> reqParam = null;
		String uEmail = null;
		try {
			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}
			try {
				reqParam = JSONUtility.deserialize(reqBody, Map.class);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			uEmail = requestParsingService.getRefinedReqVal(reqParam.get("uEmail"));
			if (reqParam == null || uEmail == null) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("Insufficient Arguments");
			}
			// check for valid email
			if (uEmail != null) {
				boolean isValidEmail = FR_MailUtils.validateEmail(uEmail);
				if (!isValidEmail) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					throw new Exception("Invalid email provided to create a new user: " + uEmail);
				}
			}
			// validate timezone
			String uTimezone = requestParsingService.getRefinedReqVal(reqParam.get("uTimezone"));
			if (uTimezone != null) {
				uTimezone = getTimeZone(uTimezone);
				if (uTimezone == null) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					throw new Exception("Invalid timezone provided to update a user:" + reqParam.get("uTimezone"));
				}
			}


			String uFirstName = requestParsingService.getRefinedReqVal(reqParam.get("uFirstName"));
			String uLastName = requestParsingService.getRefinedReqVal(reqParam.get("uLastName"));
			String uCompany = requestParsingService.getRefinedReqVal(reqParam.get("uCompany"));

			UserAPIResponse apiRes = userService.createUser(Long.parseLong(UserInfoThreadLocal.get().getUserId()), uEmail, uFirstName,
					uLastName, uTimezone, null, uCompany);
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in updateUserById api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				res = responseDecoratorService.getUserResponse(apiRes, "create.user.succ");
			} else {
				throw new Exception("updateUserById api returned null.");
			}

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "adduser");
			request.setAttribute("metaData", reqBody);
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

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value = "/{userId}/update", method = {RequestMethod.PUT, RequestMethod.POST}, headers = "Accept=application/json")
	public JSONResponse update(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody,
			@PathVariable("userId") String userIdParam) {
		String userId = userIdParam;
		UserWrapperResponse res = null;
		int errorCode = -1;

		Map<String, String> reqParam = null;
		String uEmail = null;
		try {
			userId = userId.split(":")[1]; // U:113
			long fruserId = -1;
			try {
				fruserId = Long.parseLong(userId);
			} catch (NumberFormatException e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}
			try {
				reqParam = JSONUtility.deserialize(reqBody, Map.class);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			uEmail = requestParsingService.getRefinedReqVal(reqParam.get("uEmail"));
			// check for valid email
			if (uEmail != null) {
				boolean isValidEmail = FR_MailUtils.validateEmail(uEmail);
				if (!isValidEmail) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					throw new Exception("Invalid email provided to update a user: " + uEmail);
				}
			}
			// validate timezone
			String uTimezone = requestParsingService.getRefinedReqVal(reqParam.get("uTimezone"));
			if (uTimezone != null) {
				uTimezone = getTimeZone(uTimezone);
				if (uTimezone == null) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					throw new Exception("Invalid timezone provided to update a user:" + reqParam.get("uTimezone"));
				}
			}

			String uStatus = requestParsingService.getRefinedReqVal(reqParam.get("uStatus"));
			if (uStatus != null && uStatus.equalsIgnoreCase(DefaultEnums.Status.DELETED.name())) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw new Exception("Invalid uStatus provided to update a user:" + uStatus);
			}

			String uFirstName = requestParsingService.getRefinedReqVal(reqParam.get("uFirstName"));
			String uLastName = requestParsingService.getRefinedReqVal(reqParam.get("uLastName"));
			String uCompany = requestParsingService.getRefinedReqVal(reqParam.get("uCompany"));

			if (reqParam == null || (uEmail == null && uFirstName == null && uLastName == null && uTimezone == null && uCompany == null
					&& uStatus == null)) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("Insufficient Arguments");
			}
			// allow only if both actor and user are same or actor should be active admin and of same account
			User actor = UserInfoThreadLocal.get();
			User user = getUser(fruserId);
			if (user == null) {
				errorCode = StatusCode.USER_NOT_FOUND;
				throw new Exception("Invalid user Id: " + fruserId);
			}
			if (fruserId != Long.parseLong(actor.getUserId()) && !isValidActor(actor, user)) {
				errorCode = StatusCode.INSUFFICIENT_PRIVILEGE;
				throw new Exception("actor should be self or ACTIVE ADMIN and of same account.");
			}
			UserAPIResponse apiRes = userService.updateUserById(UserInfoThreadLocal.get(), fruserId, uEmail, uFirstName, uLastName,
					uTimezone, uStatus, uCompany);
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in updateUserById api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				res = responseDecoratorService.getUserResponse(apiRes, "edit.user.succ");
			} else {
				throw new Exception("updateUserById api returned null.");
			}

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "updateuser");
			request.setAttribute("targetId", "U:" + userId);
			request.setAttribute("metaData", reqBody);
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

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{userId}/monitors", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse monitorDetails(HttpServletRequest request, HttpServletResponse response, @PathVariable("userId") String userId) {
		MonitorDetailsResponse res = new MonitorDetailsResponse();
		int errorCode = -1;
		try {
			String uId = userId.split(":")[1]; // U:113
			long fruserId = -1;
			try {
				fruserId = Long.parseLong(uId);
			} catch (NumberFormatException e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			// allow only if both actor and user are same or actor should be active admin and of same account
			User actor = UserInfoThreadLocal.get();
			User user = getUser(fruserId);
			if (user == null) {
				errorCode = StatusCode.USER_NOT_FOUND;
				throw new Exception("Invalid user Id: " + fruserId);
			}
			if (fruserId != Long.parseLong(actor.getUserId()) && !isValidActor(actor, user)) {
				errorCode = StatusCode.INSUFFICIENT_PRIVILEGE;
				throw new Exception("actor should be self or ACTIVE ADMIN and of same account.");
			}
			MonitorAPIResponse apiRes = monitorService.getMonitorListByOwner(UserInfoThreadLocal.get(), user, OwnedByType.USER.name());
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in monitordetails api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null && apiRes.getMonitorDetails() != null) {
				res = responseDecoratorService.getMonitorDetailsResponse(apiRes.getMonitorDetails());
			} else {
				if (apiRes != null) {
					errorCode = apiRes.getStatusCode();
				}
				throw new Exception("monitordetails api returned null, errorCode: " + errorCode);
			}
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "monitordetails");
			request.setAttribute("targetId", userId);
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

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/getUserIdFromEmail", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse getId(HttpServletRequest request, HttpServletResponse response, @RequestParam("uEmail") String uEmail) {
		UserIdWrapperResponse res = null;
		int errorCode = -1;
		try {
			UserAPIResponse apiRes = null;
			// check for valid email
			long fruserId = -1;
			User actor = UserInfoThreadLocal.get();
			if (uEmail != null) {
				boolean isValidEmail = validateEmail(uEmail);
				if (isValidEmail) {
					apiRes = userService.getUserByEmail(uEmail, actor.getOwnedBy());
					fruserId = retrieveFrUserId(apiRes, fruserId); 
					
				} else {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
				}
			}

			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in getUserIdFromEmail api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				// allow only if both actor and user are same or actor should be active admin and of same account
				if (fruserId == Long.parseLong(actor.getUserId()) || isValidActor(actor, apiRes.getUser())) {
					res = responseDecoratorService.getUserIdResponse(apiRes, "gen.succ");
				} else {
					errorCode = StatusCode.INSUFFICIENT_PRIVILEGE;
					throw new Exception("actor should be self or ACTIVE ADMIN and of same account.");
				}
			} else {
				throw new Exception("getUserIdFromEmail api returned null.");
			}
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "getuseridfromemail");
			request.setAttribute("targetId", uEmail);
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
 
	private long retrieveFrUserId(final UserAPIResponse apiRes, long fruserIdParam) { 
		long fruserId = fruserIdParam;
		if (apiRes != null && apiRes.getUser() != null) { 
			fruserId = Long.parseLong(apiRes.getUser().getUserId()); 
		} 
		return fruserId; 
	} 
	

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value = "/delete", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse delete(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody) {
		JSONResponse res = null;
		int errorCode = -1;
		try {
			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}
			Map<String, String> reqParam = null;
			try {
				reqParam = JSONUtility.deserialize(reqBody, Map.class);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			String uEmail = requestParsingService.getRefinedReqVal(reqParam.get("uEmail"));
			String uId = requestParsingService.getRefinedReqVal(reqParam.get("uId"));
			// check for valid email
			UserAPIResponse apiRes = null;
			long fruserId = -1;
			User actor = UserInfoThreadLocal.get();
			if (uEmail != null) {
				boolean isValidEmail = validateEmail(uEmail);
				if (isValidEmail) {
					apiRes = userService.getUserByEmail(uEmail, actor.getOwnedBy(), true);
					if (apiRes != null && apiRes.getUser() != null) {
						fruserId = Long.parseLong(apiRes.getUser().getUserId());
					}
				} else {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
				}
			} else if (uId != null) {
				try {
					fruserId = Long.parseLong(uId.split(":")[1]);
				} catch (Exception e) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					throw e;
				}
				apiRes = userService.getUserById(fruserId, true);
			}

			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in delete user api, error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				// allow only if actor is active admin and of same account
				if (isValidActor(actor, apiRes.getUser()) && apiRes.getUser().getMembershipType() != MembershipType.ADMIN) {
					UserAPIResponse apiRes1 = userService.deleteUser(fruserId);
					if (apiRes1.getStatusCode() == StatusCode.REQUEST_SUCCESS) {
						res = responseDecoratorService.getDeleteUserResponse("delete.user.succ");
					}
				} else {
					errorCode = StatusCode.INSUFFICIENT_PRIVILEGE;
					throw new Exception("actor should be self or ACTIVE ADMIN and of same account.");
				}
			} else {
				throw new Exception("getUserIdFromEmail api returned null.");
			}
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "deleteuser");
			request.setAttribute("targetId", uEmail);
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

	private String getTimeZone(String timeZone) {
		if (timeZone != null) {
			for (String item : timezones.split(",")) {
				if (timeZone.equalsIgnoreCase(item)) {
					return item;
				}
			}
		}
		return null;
	}

	private User getUser(long fruserId) throws Exception {
		User user = null;
		UserAPIResponse apiRes = userService.getUserById(fruserId);
		if (apiRes != null && apiRes.getStatusCode() == 200) {
			user = apiRes.getUser();
		}
		return user;
	}

	private boolean isValidActor(User actor, User user) {
		boolean isValid = false;
		// actor should be active admin and of same account as of user
		if (DefaultEnums.Status.ACTIVE.name().equalsIgnoreCase(actor.getFlags()) && MembershipType.ADMIN.equals(actor.getMembershipType())
				&& actor.getOwnedBy() == user.getOwnedBy()) {
			isValid = true;
		}
		return isValid;
	}

	private boolean validateEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			return false;
		}
		try {
			new InternetAddress(email).validate();
		} catch (Exception e) {
			// LOG.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
}
