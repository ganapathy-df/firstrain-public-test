/**
 * 
 */
package com.firstrain.web.controller.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.domain.Brand;
import com.firstrain.web.domain.DocCategorizePwResponse;
import com.firstrain.web.pojo.CategorizerObject;
import com.firstrain.web.response.CategorizationServiceResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.PWBrandMapping;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.util.EnterpriseConfigThreadLocal;
import com.firstrain.web.util.ProjectConfig.EnterpriseConfig;

/**
 * @author vgoyal
 *
 */
@Controller
@RequestMapping(value = "/categorizepw")
public class CategorizerPwController {
	private static final Logger LOG = Logger.getLogger(CategorizerPwController.class);
	private static final String TAXONOMY_DIRECTIVE = "taxonomyDirective";
	private static final String TARGET_SEARCH_TOKENS = "targetSearchTokens";

	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private HttpClientService httpClientService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private PWBrandMapping pwBrandMapping;

	@Value("${categorization.service.url}")
	private String catServiceURL;

	@Value("${local.file.path:}")
	private String localFilePath;

	private static AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis());

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value = "/doc", method = RequestMethod.POST)
	public JSONResponse doc(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody) {
		CategorizationServiceResponse res = null;
		int errorCode = -1;
		String docId = null;
		String errorMsg = null;
		try {
			Map<String, Object> reqParam = null;
			String title = null;
			String body = null;
			List<String> taxonomyDirectives = new ArrayList<String>();
			List<String> targetSearchTokens = new ArrayList<String>();
			try {
				LOG.debug("Request Body : " + reqBody);
				reqParam = JSONUtility.deserialize(reqBody, Map.class);
				docId = requestParsingService.getRefinedReqVal(reqParam.get("docId"));
				title = requestParsingService.getRefinedReqVal(reqParam.get("title"));
				body = requestParsingService.getRefinedReqVal(reqParam.get("body"));
				getTokens(reqParam, taxonomyDirectives, TAXONOMY_DIRECTIVE);
				getTokens(reqParam, targetSearchTokens, TARGET_SEARCH_TOKENS);

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

			LOG.info("categorizepw/doc processing for taxonomyDirectives : " + taxonomyDirectives);
			String taxonomyDirective = taxonomyDirectives.get(0);
			String brandKey = pwBrandMapping.getPwKey(taxonomyDirective != null ? taxonomyDirective.toLowerCase() : null);

			if (StringUtils.isEmpty(brandKey)) {
				errorMsg = "TaxonomyDirective is incorrect.";
				errorCode = 400;
				throw new Exception("taxonomy directive is not correct.");
			}

			Brand brand = pwBrandMapping.getBrand(brandKey);

			if (brand == null) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw new Exception("Illegal Arguments");
			}
			brand.setBrandInitials(pwBrandMapping.getPwBrandInitials(brandKey));
			reqMap.put("entities", brand.getTopicMap().keySet());
			reqMap.put("includeCustomerEntities", Boolean.TRUE);

			CategorizerObject results = null;
			try {

				if (StringUtils.isEmpty(localFilePath)) {
					results = httpClientService.postDataInReqBody(catServiceURL, "getCategorizedDoc", reqMap, CategorizerObject.class);
				} else {
					ObjectMapper jsonMapper = new ObjectMapper();
					results = jsonMapper.readValue(new File(localFilePath), CategorizerObject.class);
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
			res = responseDecoratorService.getEntityListResponseForCategorizePw(results, docId, brand, taxonomyDirectives, brandKey,
					targetSearchTokens);

			// for storing data into db
			DocCategorizePwResponse docCategorizePwResponse = new DocCategorizePwResponse();
			docCategorizePwResponse.setActualresponse(res);
			docCategorizePwResponse.setServiceRes(results);
			docCategorizePwResponse.setTaxonomyDirective(taxonomyDirectives);

			// Own response decorator
			// + Usagetracking like interceptor which will store response in db

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "categorizedocpw");
			request.setAttribute("targetId", docId);
			String metadata = JSONUtility.serialize(docCategorizePwResponse);
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
			return requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, docId, errorMsg);
		}
		return res;
	}

	public void getTokens(Map<String, Object> reqParam, List<String> tokens, String param) {
		Object TokensObj = reqParam.get(param);
		if (TokensObj != null) {
			List<String> tempTokens = (List<String>) TokensObj;
			for (String token : tempTokens) {
				tokens.add(token.trim().toLowerCase());
			}
		}
	}
}
