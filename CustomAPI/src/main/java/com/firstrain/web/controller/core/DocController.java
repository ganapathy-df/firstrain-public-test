/**
 * 
 */
package com.firstrain.web.controller.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.frapi.customapiservice.TakeDownService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.EnterpriseConfigThreadLocal;

/**
 * @author vgoyal
 *
 */
@Controller
@RequestMapping(value = "/doc")
public class DocController {
	private static final Logger LOG = Logger.getLogger(DocController.class);

	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private TakeDownService takeDownService;
	@Autowired
	private RequestParsingService requestParsingService;
	

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{docId}/takeDown", method = RequestMethod.POST)
	public JSONResponse barAll(HttpServletRequest request, HttpServletResponse response, @PathVariable("docId") String docId,
			@RequestParam(value = "forcedFlag", required = false, defaultValue="false") boolean forcedFlag) {
		JSONResponse res = null;
		int errorCode = -1;
		try {
			long articleId = -1;
			String[] temp = docId.split(":");
			if (temp.length == 2) {
				try {
					articleId = Long.parseLong(temp[1]);
				} catch (NumberFormatException e) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					throw e;
				}
			} else {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw new Exception("Invalid docId passed: " + docId);
			}

			long enterpriseId = EnterpriseConfigThreadLocal.get().getId();
			int statusCode = -1;
			statusCode = takeDownService.takeDownContent(enterpriseId, articleId);
			/*
			 * if(forcedFlag) { statusCode = takeDownService.takeDownContentForced(enterpriseId, articleId); } else { statusCode =
			 * takeDownService.takeDownContent(enterpriseId, articleId); }
			 */
			res = responseDecoratorService.getTakeDownResponse(statusCode, "takedown.success");
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "takeDown");
			request.setAttribute("targetId", docId);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return requestParsingService.getErrorResponse(errorCode);
		}
		return res;
	}

	/*
	 * @SuppressWarnings("rawtypes")
	 * 
	 * @RequestMapping(value= "/{docId}/status", method = RequestMethod.POST) public JSONResponse status(HttpServletRequest request,
	 * HttpServletResponse response,
	 * 
	 * @PathVariable("docId") String docId) { JSONResponse res = null; int errorCode = -1; try { int statusCode =
	 * staticDataService.getDataObject("bar.txt", JSONResponse.class); //status code - 200 -> takedown.status.success //status code - 124 ->
	 * errorcode.124 //status code - tbd -> takedown.status.processing
	 * 
	 * res = responseDecoratorService.getTakeDownResponse(statusCode, "takedown.status.success"); } catch (Exception e) {
	 * LOG.error(e.getMessage(), e); return requestParsingService.getErrorResponse(errorCode); } return res; }
	 */
}
