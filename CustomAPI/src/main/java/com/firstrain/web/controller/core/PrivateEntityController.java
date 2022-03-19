package com.firstrain.web.controller.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.firstrain.db.obj.EntityBacktest;
import com.firstrain.db.obj.PrivateEntity;
import com.firstrain.db.obj.PrivateEntityList;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.domain.UpdateDefinition;
import com.firstrain.web.exception.CustomExceptionError;
import com.firstrain.web.exception.CustomExceptionSuccess;
import com.firstrain.web.pojo.CategorizerEntityObject;
import com.firstrain.web.pojo.CreateInputBean;
import com.firstrain.web.pojo.PennwellUserActivityInputBean;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.EntiyBackTestService;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.PrivateEntityService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.wrapper.PrivateEntityWrapper;

@Controller
@RequestMapping(value = "/privateEntity")
public class PrivateEntityController {
	private static final Logger LOG = Logger.getLogger(PrivateEntityController.class);
	private final ObjectMapper jsonMapper = new ObjectMapper();

	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private PrivateEntityService privateEntityService;
	@Autowired
	private EntiyBackTestService entityBackTestService;
	@Autowired
	private HttpClientService httpClientService;

	@Value("${categorization.service.url}")
	private String catServiceURL;

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	private JSONResponse create(HttpServletRequest request, @RequestBody String body) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		PrivateEntityWrapper privateEntityWrapper = null;
		List<PrivateEntity> privateEntities = new ArrayList<PrivateEntity>();
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (IOException ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithTrackingParams(request, inputbean.getName(), "create");

			// validate
			requestParsingService.validatePrivateEntityInputFields(inputbean, false);
			String definition = jsonMapper.readTree(body).get("definition").toString();
			Map<String, Object> reqMap = new HashMap<String, Object>();
			populateDefinitionAndValidateResults(reqMap, definition); 

			String searchToken = privateEntityService.generateSearchToken(inputbean);
			privateEntityService.createPrivateEntity(searchToken, inputbean, definition, privateEntities);
			// using service post data into table
			
			if (CollectionUtils.isNotEmpty(privateEntities)) {
				PrivateEntity privateEntity = privateEntities.get(0);
				privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapper(privateEntity.getSearchToken(),
						privateEntity.getName(), privateEntity.getInsertTime());
			}

			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "topic.created.successfully");

			// Usagetracking
			request.setAttribute("targetId", searchToken);
			PennwellUserActivityInputBean useractivityInputbean = JSONUtility.deserialize(body, PennwellUserActivityInputBean.class);
			metadata = JSONUtility.serialize(useractivityInputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage(),
					customExpErr.isCustomMessage());
		} catch (CustomExceptionSuccess customExp) {
			errorCode = customExp.getErrorCode();
			if (errorCode == 119) {
				if (CollectionUtils.isNotEmpty(privateEntities)) {
					PrivateEntity privateEntity = privateEntities.get(0);
					privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapper(privateEntity.getSearchToken(),
							privateEntity.getName(), privateEntity.getInsertTime());
				}
				res = responseDecoratorService.getPrivateEntityServiceResponse(119, privateEntityWrapper, null);
			} else {
				res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, null, null);
			}
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}

	private JSONResponse errorcaseLogInUsageTracking(HttpServletRequest request, JSONResponse resParam, int errorCode, String errorMsg,
			CreateInputBean inputbean, String metadata) {
		// Usagetracking logging error case in metadata
		JSONResponse res = resParam;
		try {
			if (StringUtils.isEmpty(metadata) && inputbean != null && res != null) {
				inputbean.setErrorMsg(res.getMessage());
				metadata = JSONUtility.serialize(inputbean);
			}
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}
		if (metadata != null && !metadata.isEmpty()) {
			request.setAttribute("metaData", metadata);
		}
		return res;
	}

	private JSONResponse getErrorResponse(int errorCodeParam, String errorMsg, Exception e) {
		int errorCode = errorCodeParam;
		if (errorCode > 0) {
			LOG.warn(e.getMessage(), e);
		} else {
			errorCode = StatusCode.INTERNAL_SERVER_ERROR;
			LOG.error(e.getMessage(), e);
		}
		return requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, errorMsg);
	}

	@RequestMapping(value = "/{searchToken}/promote", method = RequestMethod.POST)
	private JSONResponse promote(HttpServletRequest request, @RequestBody String body, @PathVariable("searchToken") String searchToken) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (IOException ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithTrackingParams(request, searchToken, "promote");

			requestParsingService.validateAuthor(inputbean);

			// promote
			UpdateDefinition updateDefinition = privateEntityService.promote(searchToken, inputbean);

			PrivateEntityWrapper privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapper(updateDefinition, false, false);

			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "topic.promoted.successfully");

			metadata = JSONUtility.serialize(inputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}
		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage());
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}

	@RequestMapping(value = "/{searchToken}/getDefinition", method = RequestMethod.POST)
	public JSONResponse getDefinition(HttpServletRequest request, @RequestBody String body,
			@PathVariable("searchToken") String searchToken) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (IOException ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithTrackingParams(request, searchToken, "getDefinition");

			requestParsingService.validateGetDefinitionInputFields(inputbean);

			UpdateDefinition ud = privateEntityService.getDefinition(searchToken);

			PrivateEntity pe = ud.getPrivateEntities().get(0);

			String definition = inputbean.getVersion().equalsIgnoreCase("DEV") ? pe.getDefinition_dev() : pe.getDefinition_live();

			if (StringUtils.isEmpty(definition)) {
				throw new CustomExceptionSuccess("version definition not present", 413);
			}

			PrivateEntityWrapper privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapper(searchToken, definition, ud,
					inputbean.getName());

			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "gen.succ");

			metadata = JSONUtility.serialize(inputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage());
		} catch (CustomExceptionSuccess customExp) {
			res = responseDecoratorService.getPrivateEntityServiceResponse(customExp.getErrorCode(), null, null);
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}

	@RequestMapping(value = "/{searchToken}/updateDefinition", method = RequestMethod.POST)
	private JSONResponse updateDefinition(HttpServletRequest request, @RequestBody String body,
			@PathVariable("searchToken") String searchToken) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (IOException ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithTrackingParams(request, searchToken, "updateDefinition");

			// validate
			requestParsingService.validatePrivateEntityInputFields(inputbean, true);
			Map<String, Object> reqMap = new HashMap<String, Object>();
			String definition = jsonMapper.readTree(body).get("definition").toString();
			populateDefinitionAndValidateResults(reqMap, definition); 
			String user = inputbean.getAuthor() != null ? inputbean.getAuthor().getName() : inputbean.getRequester().getName();
			String email = inputbean.getAuthor() != null ? inputbean.getAuthor().getEmail() : inputbean.getRequester().getEmail();
			UpdateDefinition updateDefinition = privateEntityService.updateDefinition(searchToken, definition, user, email);

			PrivateEntityWrapper privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapper(updateDefinition, false, false);

			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "topic.updated.successfully");

			PennwellUserActivityInputBean useractivityInputbean = JSONUtility.deserialize(body, PennwellUserActivityInputBean.class);
			metadata = JSONUtility.serialize(useractivityInputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage(),
					customExpErr.isCustomMessage());
		} catch (CustomExceptionSuccess customExp) {
			res = responseDecoratorService.getPrivateEntityServiceResponse(customExp.getErrorCode(), null, null);
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}
 
	private void populateDefinitionAndValidateResults(final Map<String, Object> reqMap, final String definition) throws Exception { 
		reqMap.put("definition", definition); 
		CategorizerEntityObject results = 
				httpClientService.postDataInReqBody(catServiceURL, "validateXml", reqMap, CategorizerEntityObject.class); 
		 
		if (results == null || results.getData() == null || !results.getData().isValid()) { 
			throw new CustomExceptionSuccess(428); 
		} 
	} 

	@RequestMapping(value = "/{searchToken}/backtest/submit", method = RequestMethod.POST)
	public JSONResponse backtestSubmit(HttpServletRequest request, @RequestBody String body,
			@PathVariable("searchToken") String searchToken) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (Exception ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithViewAndActivityType(request);
			if (inputbean.getDocs() != null) {
				request.setAttribute("target", "backtestSubmitWithDocs");
			} else {
				request.setAttribute("target", "backtestSubmit");
			}
			request.setAttribute("targetId", searchToken);

			// using service post data into table
			long job_id = privateEntityService.backTestSubmit(searchToken,
					inputbean.getDocs() != null ? JSONUtility.serialize(inputbean.getDocs()) : null);

			PrivateEntityWrapper privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapperByFileds(searchToken,
					String.valueOf(job_id), null, inputbean.getName(), null);

			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "gen.succ");

			PennwellUserActivityInputBean useractivityInputbean = JSONUtility.deserialize(body, PennwellUserActivityInputBean.class);
			metadata = JSONUtility.serialize(useractivityInputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage());
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}

	@RequestMapping(value = "/backtest/{jobId}/check", method = RequestMethod.POST)
	public JSONResponse backtestCheck(HttpServletRequest request, @RequestBody String body, @PathVariable("jobId") long jobId) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (Exception ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithViewAndActivityType(request);
			request.setAttribute("target", "backtestCheck");
			request.setAttribute("targetId", jobId);

			// using service post data into table
			EntityBacktest entityBacktest = privateEntityService.backTestCheck(jobId);

			PrivateEntityWrapper privateEntityWrapper = responseDecoratorService.populatePrivateEntityWrapper(entityBacktest);
			privateEntityWrapper.setJobId(String.valueOf(jobId));

			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "gen.succ");

			metadata = JSONUtility.serialize(inputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage());
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}

	private void populateRequestWithViewAndActivityType(HttpServletRequest request) {
		request.setAttribute("loadview", true);
		request.setAttribute("activityType", request.getMethod());
	}

	@RequestMapping(value = "/backtest/{jobId}/kill", method = RequestMethod.POST)
	public JSONResponse backtestKill(HttpServletRequest request, @RequestBody String body, @PathVariable("jobId") long jobId) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (Exception ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithViewAndActivityType(request);
			request.setAttribute("target", "backtestkill");
			request.setAttribute("targetId", jobId);


			EntityBacktest entityBacktest = entityBackTestService.updateState(jobId);
			res = responseDecoratorService.getEntityBacktestServiceResponse(errorCode, "job.updated.successfully");

			metadata = JSONUtility.serialize(inputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage());
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}

	@RequestMapping(value = "/{searchToken}/updateState", method = RequestMethod.POST)
	public JSONResponse updateState(HttpServletRequest request, @RequestBody String body, @PathVariable("searchToken") String searchToken) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (IOException ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithTrackingParams(request, searchToken, "updateState");

			String state = inputbean.getState();
			requestParsingService.validateState(state);

			UpdateDefinition updateDefinition = privateEntityService.updateState(searchToken, state);
			PrivateEntityWrapper privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapper(updateDefinition, true, false);
			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "topic.updated.successfully");

			metadata = JSONUtility.serialize(inputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage());
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public JSONResponse list(HttpServletRequest request, @RequestBody String body) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (IOException ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithTrackingParams(request, inputbean.getTaxonomyDirective(), "list");

			requestParsingService.validateTaxonomyDirectiveForList(inputbean);

			List<PrivateEntityList> privateEntities = privateEntityService
					.getPrivateEntityListByTaxonomyDirective(inputbean.getTaxonomyDirectiveLst());
			
			if (CollectionUtils.isEmpty(privateEntities)) {
				// throw new CustomExceptionError(118);
			}

			PrivateEntityWrapper privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapper(privateEntities);

			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "gen.succ");

			metadata = JSONUtility.serialize(inputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}

		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage());
		} catch (CustomExceptionSuccess customExp) {
			res = responseDecoratorService.getPrivateEntityServiceResponse(customExp.getErrorCode(), null, null);
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/{searchToken}/lookupDefinition", method = RequestMethod.POST)
	public JSONResponse lookupDefinition(HttpServletRequest request, HttpServletResponse response, @RequestBody String body,
			@PathVariable("searchToken") String searchToken) {
		JSONResponse res = null;
		int errorCode = -1;
		String errorMsg = null;
		CreateInputBean inputbean = null;
		String metadata = null;
		try {

			try {
				// body deserilaize
				inputbean = JSONUtility.deserialize(body, CreateInputBean.class);
			} catch (IOException ex) {
				return requestParsingService.getErrorResponseForCatgorizerService(422, null, null, null);
			}

			// Usagetracking
			populateRequestWithTrackingParams(request, searchToken, "lookupDefinition");

			// validate
			requestParsingService.validateSearchTokenFromEntityInfoCache(searchToken, true, inputbean);
			requestParsingService.validateAuthor(inputbean);

			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("token", searchToken);
			String results = httpClientService.getData(catServiceURL, "getCategoryJson", reqMap);

			if (StringUtils.isEmpty(results) || jsonMapper.readTree(results).get("data") == null
					|| jsonMapper.readTree(results).get("data").get("rule") == null) {
				throw new CustomExceptionSuccess(415);
			}

			String rule = jsonMapper.readTree(results).get("data").get("rule").toString();
			PrivateEntityWrapper privateEntityWrapper = responseDecoratorService.getPrivateEntityWrapper(searchToken, rule, null, null);
			privateEntityWrapper.setName(inputbean.getName());

			res = responseDecoratorService.getPrivateEntityServiceResponse(errorCode, privateEntityWrapper, "gen.succ");


			PennwellUserActivityInputBean useractivityInputbean = JSONUtility.deserialize(body, PennwellUserActivityInputBean.class);
			metadata = JSONUtility.serialize(useractivityInputbean);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}
		} catch (CustomExceptionError customExpErr) {
			errorCode = retrieveErrorCode(customExpErr); 
			res = requestParsingService.getErrorResponseForCatgorizerService(errorCode, null, null, customExpErr.getMessage());
		} catch (CustomExceptionSuccess customExp) {
			res = responseDecoratorService.getPrivateEntityServiceResponse(customExp.getErrorCode(), null, null);
		} catch (Exception e) {
			res = getErrorResponse(errorCode, errorMsg, e);
		}

		res = errorcaseLogInUsageTracking(request, res, errorCode, errorMsg, inputbean, metadata);

		return res;
	}
 
	private int retrieveErrorCode(final CustomExceptionError customExpErr) { 
		int errorCode = customExpErr.getErrorCode(); 
		if (errorCode <= 0) { 
			errorCode = StatusCode.INTERNAL_SERVER_ERROR; 
		} 
		return errorCode; 
	} 

	private void populateRequestWithTrackingParams(HttpServletRequest request,
			String searchToken, String lookupDefinition) {
		populateRequestWithViewAndActivityType(request);
		request.setAttribute("target", lookupDefinition);
		request.setAttribute("targetId", searchToken);
	}
}
