/**
 * 
 */
package com.firstrain.web.controller.core;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.firstrain.frapi.customapirepository.impl.DocCategorizeRepositoryImpl;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.pojo.CategorizerObject;
import com.firstrain.web.response.CategorizationServiceResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.core.EnterpriseConfigLoader;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.EnterpriseConfigThreadLocal;
import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;

/**
 * @author vgoyal
 *
 */
@Controller
@RequestMapping(value = "/categorize")
public class CategorizerController {
	private static final Logger LOG = Logger.getLogger(CategorizerController.class);

	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private HttpClientService httpClientService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private DocCategorizeRepositoryImpl docCategorizeRepositoryImpl;
	@Autowired
	private ResourceBundleMessageSource messageSource;
	@Autowired
	private EnterpriseConfigLoader enterpriseConfigLoader;
	@Value("${categorization.service.url}")
	private String catServiceURL;
	private static AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value = "/doc/{id}", method = RequestMethod.POST)
	public JSONResponse doc(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String guid,
			@RequestBody String reqBody) {
		CategorizationServiceResponse res = null;
		int errorCode = -1;
		try {
			Map<String, String> reqParam = null;
			String title = null;
			String body = null;
			try {
				reqParam = JSONUtility.deserialize(reqBody, Map.class);
				title = requestParsingService.getRefinedReqVal(reqParam.get("title"));
				body = requestParsingService.getRefinedReqVal(reqParam.get("body"));
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}

			if (title == null || body == null) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("Insufficient Arguments");
			}
			// res = staticDataService.getDataObject("entitylist.txt", EntityListResponse.class);
			Map<String, Object> reqMap = new HashMap<String, Object>();
			reqMap.put("docId", atomicLong.getAndIncrement());

			EnterpriseConfig ec = EnterpriseConfigThreadLocal.get();
			if (ec != null) {
				reqMap.put("appId", ec.getAppname());
				reqMap.put("industryClassificationId", ec.getIndclassid());
				if (ec.getTopicdimensionList() != null) {
					reqMap.put("requiredTopicDim", ec.getTopicdimensionList());
				}
				request.setAttribute("str2", ec.getAccesstoken());
			}

			reqMap.put("title", title);
			reqMap.put("body", body);

			CategorizerObject results = null;
			try {
				results = httpClientService.postDataInReqBody(catServiceURL, "getCategorizedDoc", reqMap, CategorizerObject.class);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			res = responseDecoratorService.getEntityListResponse(results, guid);

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "categorizedoc");
			request.setAttribute("targetId", guid);
			String metadata = requestParsingService.getSerializedMetadata(reqMap);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (Exception e) {
			if (errorCode > 0) {
				LOG.warn(e.getMessage(), e);
			} else {
				errorCode = StatusCode.INTERNAL_SERVER_ERROR;
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponseForCatgorizerService(errorCode, guid, null);
		}
		return res;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value = "/feedback", method = RequestMethod.POST)
	public JSONResponse feedback(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody) {
		JSONResponse res = new JSONResponse();
		try {
			// docCategorizeRepositoryImpl.insertIntoDocCategorizeFeedback(reqBody);
			res.setStatus(ResStatus.SUCCESS);
			res.setMessage(messageSource.getMessage("feedback.saved.successfully", null, Locale.getDefault()));
			res.setVersion(responseDecoratorService.getVersion());

			EnterpriseConfig ec = EnterpriseConfigThreadLocal.get();
			if (ec != null) {
				request.setAttribute("str2", ec.getAccesstoken());
			}
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "categorizefeedback");
			String metadata = reqBody;
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			res = requestParsingService.getErrorResponseForCatgorizerService(StatusCode.INTERNAL_SERVER_ERROR, null, null);
		}
		return res;
	}

}
