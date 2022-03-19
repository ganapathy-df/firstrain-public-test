/**
 * 
 */
package com.firstrain.web.controller.core;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.frapi.pojo.EmailResponse;
import com.firstrain.frapi.service.EmailService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.UserInfoThreadLocal;

/**
 * @author mli
 *
 */
@Controller
@RequestMapping(value = "/email")
public class EmailController {
	private static final Logger LOG = Logger.getLogger(EmailController.class);

	@Autowired
	private FreemarkerTemplateService ftlService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private RequestParsingService requestParsingService;


	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/brief", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse getEmailBrief(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String emailId,
			@RequestParam(value = "results", required = false, defaultValue = "D") String resultsCSV) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {

			long emailIdL = -1;
			try {
				emailIdL = Long.parseLong(emailId.split(":")[1]);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("Invalid itemId passed in the url: " + emailId);
				throw new Exception();
			}

			EmailResponse apiRes = emailService.getEmailDetails(UserInfoThreadLocal.get(), emailIdL);
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in email detail api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				entityDataResponse = responseDecoratorService.getEmailResponse(apiRes, "gen.succ");
			} else {
				throw new Exception("getEmailBrief api returned null.");
			}

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("targetId", emailId);
			request.setAttribute("target", "emailBrief");

		} catch (Exception e) {
			if (errorCode > 0) {
				LOG.warn(e.getMessage());
			} else {
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return entityDataResponse;
	}


}
