/**
 * 
 */
package com.firstrain.web.controller.core;

import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.MonitorAPIResponse;
import com.firstrain.frapi.pojo.MonitorEmailAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.pojo.wrapper.GetBulk;
import com.firstrain.frapi.service.MonitorBriefService;
import com.firstrain.frapi.service.MonitorService;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.exception.ControllerOperationException;
import com.firstrain.web.helper.MonitorControllerHelper;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.EntityResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.MonitorConfigResponse;
import com.firstrain.web.response.MonitorInfoResponse;
import com.firstrain.web.response.MonitorWrapperResponse;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.service.core.StorageService;
import com.firstrain.web.util.UserInfoThreadLocal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author gkhanchi
 *
 */
@Controller
@RequestMapping(value = "/monitor")
public class MonitorController {

	private static final Logger LOG = Logger.getLogger(MonitorController.class);
	private static final String VIEW_FTL = "monitor-brief.ftl";
	private static final String COLON = ":";

	@Autowired
	private FreemarkerTemplateService ftlService;
	@Autowired
	private MonitorService monitorService;
	@Autowired
	private MonitorBriefService monitorBriefService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private StorageService storageService;


	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/brief", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse getResponseInAllFormat(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String monitorId,
			@RequestParam(value = "sections", required = true) String sections, @RequestParam(value = "fq", required = false) String fq,
			@RequestParam(value = "debug", required = false) boolean debug,
			@RequestParam(value = "htmlFrag", required = false, defaultValue = "classicNoFrame") String htmlFrag,
			@RequestParam(value = "results", required = false, defaultValue = "D") String resultsCSV,
			@RequestParam(value = "needPhrases", required = false, defaultValue = "false") boolean needPhrases,
			@RequestParam(value = "entitylinks", required = false, defaultValue = "false") boolean entityLinking) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			EnterprisePref enterprisePref = null;
			Map<SectionType, SectionSpec> sectionsMap = null;
			Set<SectionType> keySet = null;
			boolean isNotExcludeTweetFields = false;

			LOG.debug("Processing Entity Brief API For Json, Input Params ->  monitorId : " + monitorId + ", sections : " + sections + ", fq : "
					+ fq + ", debug : " + debug + ", htmlFrag : " + htmlFrag + ", results : " + resultsCSV + ", needPhrases : " + needPhrases
					+ ", entitylinks : " + entityLinking);
			final MonitorControllerHelper monitorControllerHelper = new MonitorControllerHelper(errorCode);
			try {
				enterprisePref = monitorControllerHelper.getEnterprisePref(requestParsingService, sections);
				sectionsMap = enterprisePref.getSectionsMap();
				keySet = monitorControllerHelper
						.validateSectionsAndUpdateRequest(sectionsMap, request, requestParsingService, sections);
			} catch (RuntimeException e) {
				errorCode = monitorControllerHelper.getErrorCode();
				throw e;
			}
			long monitorIdL = -1;
			try {
				monitorIdL = Long.parseLong(monitorId.split(COLON)[1]);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("Invalid itemId passed in the url: " + monitorId);
				throw new Exception();
			}

			// fetch visualization for results type H also to check and add content availability
			// flag with
			// visualization web widgets
			sectionsMap.put(SectionType.VIZ, null);

			// for json format return nodelist even if single mode is present
			enterprisePref.setApplyMinNodeCheckInVisualization(false);

			MonitorAPIResponse apiRes = monitorBriefService.getMonitorBriefDetails(UserInfoThreadLocal.get(), monitorIdL, enterprisePref, fq);
			if (apiRes != null && apiRes.getStatusCode() != 200 && apiRes.getStatusCode() != StatusCode.NO_ITEMS_IN_MONITOR) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in monitorbrief api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {

				isNotExcludeTweetFields = responseDecoratorService.excludeTweetInfo(UserInfoThreadLocal.get().getOwnedBy());
				boolean excludeTweetInfo = (StringUtils.isNotEmpty(resultsCSV) && (resultsCSV.contains("H") || resultsCSV.contains("h")))
						? false : isNotExcludeTweetFields;

				if ((needPhrases || entityLinking) && apiRes.getMonitorBriefDetail() != null) {

					List<String> ids = new ArrayList<String>();
					List<String> wrIds = storageService.getListOfIdsfromDocumentSet(apiRes.getMonitorBriefDetail().getWebResults());
					if (CollectionUtils.isNotEmpty(wrIds)) {
						ids.addAll(wrIds);
					}
					Map<String, GetBulk> map = storageService.getDocFieldsFromStorageService(ids, needPhrases, entityLinking);

					if (MapUtils.isNotEmpty(map)) {
						storageService.populateFieldInDocSet(map, apiRes.getMonitorBriefDetail().getWebResults());
					}
				}

				entityDataResponse = responseDecoratorService.getEntityDataResponse(apiRes, "gen.succ", sectionsMap,
						enterprisePref.getIndustryClassificationId(), excludeTweetInfo);
			} else {
				throw new Exception("createmonitor api returned null.");
			}

			if (resultsCSV != null && !resultsCSV.isEmpty() && entityDataResponse != null) {
				final MonitorControllerHelper helper = new MonitorControllerHelper(-1);
				helper.setFtlService(ftlService);
				helper.setRequestParsingService(requestParsingService);
				helper.setResponseDecoratorService(responseDecoratorService);
				helper.updateEntityResponseData(request, response, monitorId, fq, debug, htmlFrag, resultsCSV,
						entityDataResponse, sectionsMap,
						keySet);

				if (!resultsCSV.contains("M") && !resultsCSV.contains("m")) {
					// if do not populate metadata then set it to null
					entityDataResponse.getResult().setMetaData(null);
				}
				if (!resultsCSV.contains("D") && !resultsCSV.contains("d")) {
					// if do not populate data then set it to null
					entityDataResponse.getResult().setData(null);
				} else if (isNotExcludeTweetFields && keySet.contains(SectionType.FT) && entityDataResponse.getResult().getData() != null
						&& entityDataResponse.getResult().getData().getFt() != null) {
					// remove Extra Tweet Fileds
					List<Tweet> tweets = entityDataResponse.getResult().getData().getFt().getTweets();
					responseDecoratorService.makeTweetsFieldsNullable(tweets);
				}
			} else {
				// if no results then always populated metadata set to null
				entityDataResponse.getResult().setMetaData(null);
			}
			// Usagetracking
			boolean isPagination = populateRequestAttributeAndGetPagination(request, monitorId, sections);

			if (resultsCSV != null && !resultsCSV.isEmpty()) {
				request.setAttribute("target", "monitorbriefwithmultipleresponsetype");
			} else if (fq != null && !fq.isEmpty()) {
				populateTargetAttribute(request, isPagination); 
				
			} else if (isPagination) {
				request.setAttribute("target", "monitorbriefwithpagingsupport");
			} else {
				request.setAttribute("target", "monitorbrief");
			}
			String metadata =
					requestParsingService.getSerializedMetadata(sections, null, fq, resultsCSV, htmlFrag, null, needPhrases, entityLinking,
							false);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}
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

	@RequestMapping(value = "/{id}/listEmails", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse getListEmails(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String monitorId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, @RequestParam(value = "debug", required = false) boolean debug) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			long monitorIdL = -1;
			try {
				monitorIdL = Long.parseLong(monitorId.split(COLON)[1]);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("Invalid itemId passed in the url: " + monitorId);
				throw new Exception();
			}

			MonitorEmailAPIResponse apiRes = monitorBriefService.getMonitorEmailList(UserInfoThreadLocal.get(), monitorIdL, startDate, endDate);
			if (apiRes != null && apiRes.getStatusCode() != 200 && apiRes.getStatusCode() != StatusCode.NO_EMAIL_SCHEDULE
					&& apiRes.getStatusCode() != StatusCode.NO_EMAIL_SENT) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in list emails api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				entityDataResponse = responseDecoratorService.getEntityDataResponse(apiRes, "gen.succ");
			} else {
				throw new Exception("getListEmails api returned null.");
			}

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("targetId", monitorId);
			request.setAttribute("target", "listEmail");

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

	@RequestMapping(value = "/{id}/brief", method = RequestMethod.GET)
	public String getResponseInHtml(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "sections", required = true) String sections, @RequestParam(value = "fq", required = false) String fq,
			@RequestParam(value = "debug", required = false) boolean debug,
			@RequestParam(value = "htmlFrag", required = false, defaultValue = "classicNoFrame") String htmlFrag,
			@PathVariable("id") String monitorId) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			EnterprisePref enterprisePref = null;
			Map<SectionType, SectionSpec> sectionsMap = null;
			Set<SectionType> keySet = null;

			LOG.debug("Processing Entity Brief API For Json, Input Params ->  monitorId : " + monitorId + ", sections : " + sections + ", fq : "
					+ fq + ", debug : " + debug + ", htmlFrag : " + htmlFrag);

			if (sections != null) {
				try {
					enterprisePref = requestParsingService.getSectionsPageSpecMap(sections);
					sectionsMap = enterprisePref.getSectionsMap();
				} catch (Exception e) {
					LOG.warn("Error parsing parameter <sections>: " + sections + e.getMessage());
					errorCode = StatusCode.INVALID_SECTION;
					throw e;
				}
			} else {
				LOG.warn("Parameter <sections> not passed in the url.");
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception();
			}
			long monitorIdL = -1;
			try {
				monitorIdL = Long.parseLong(monitorId.split(COLON)[1]);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("Invalid itemId passed in the url: " + monitorId);
				throw new Exception();
			}

			Map<String, Object> ftlParams = new HashMap<String, Object>();
			if (sectionsMap != null) {
				keySet = sectionsMap.keySet();
				String sectionId = null;
				if (keySet != null) {
					sectionId = keySet.toString();
				}
				request.setAttribute("sectionId", sectionId);
				Set<SectionType> tempSet = requestParsingService.intersectSets(keySet, requestParsingService.getAllSectionIDs());
				if (tempSet.isEmpty()) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					LOG.warn("Invalid value passed in parameter <sections> : " + sections);
					throw new Exception();
				}
				if (keySet.contains(SectionType.valueOf("FR"))) {
					ftlParams.put("showFR", true);
				}
				if (keySet.contains(SectionType.valueOf("FT"))) {
					ftlParams.put("showFT", true);
				}
			}

			// fetch visualization to check and add content availability response headers for
			// visualization web widgets
			sectionsMap.put(SectionType.VIZ, null);

			MonitorAPIResponse apiRes = monitorBriefService.getMonitorBriefDetails(UserInfoThreadLocal.get(), monitorIdL, enterprisePref, fq);
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in monitorbrief api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				entityDataResponse = responseDecoratorService.getEntityDataResponse(apiRes, "gen.succ", sectionsMap,
						enterprisePref.getIndustryClassificationId(), false);
				// handling visualization
				responseDecoratorService.setChartDataForHtml(keySet, entityDataResponse, monitorId, sectionsMap, debug, fq, response);
			} else {
				throw new Exception("createmonitor api returned null.");
			}

			ftlParams.put("obj", entityDataResponse);
			if (htmlFrag.equalsIgnoreCase("classicFrame")) {
				ftlParams.put("showheading", "true");
			} else {
				ftlParams.put("showheading", "false");
			}
			ftlParams.put("reqScheme", requestParsingService.getRequestScheme(request));
			String html = ftlService.getHtml(VIEW_FTL, ftlParams);
			model.addAttribute("htmlContent", html);

			// Usagetracking
			boolean isPagination = populateRequestAttributeAndGetPagination(request, monitorId, sections);

			if (fq != null && !fq.isEmpty()) {
				populateTargetAttribute(request, isPagination); 
				
			} else if (isPagination) {
				request.setAttribute("target", "monitorbriefwithpagingsupport");
			} else {
				request.setAttribute("target", "monitorbrief");
			}
			String metadata = requestParsingService.getSerializedMetadata(sections, null, fq, null, htmlFrag);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}
		} catch (Exception e) {
			if (errorCode < 0) {
				LOG.error(e.getMessage(), e);
			}
			model.addAttribute("errorMsg", requestParsingService.getErrorHtmlResponse(errorCode));
		}
		return "view";
	}

	private boolean populateRequestAttributeAndGetPagination(final HttpServletRequest request, final String monitorId, final String sections) {
		request.setAttribute("loadview", true);
		request.setAttribute("activityType", request.getMethod());
		request.setAttribute("targetId", monitorId);
		
		boolean isPagination = false;
		if (sections != null && sections.contains("io:")) {
			isPagination = true;
		}
		return isPagination;
	}
 
	private void populateTargetAttribute(final HttpServletRequest request, final boolean isPagination) { 
		request.setAttribute("target", "monitorbriefwithfilteringsupport"); 
		if (isPagination) { 
			request.setAttribute("target", "monitorbriefwithpagingandfilteringsupport"); 
		} 
	} 
	

	@SuppressWarnings({"rawtypes", "unchecked"})
	@RequestMapping(value = "/create", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse create(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody) {
		MonitorWrapperResponse res = null;
		int errorCode = -1;
		try {
			Map<String, Object> reqParam = null;
			String monitorName = null;
			List<String> entityList = null;
			List<String> filterList = null;

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

			monitorName = (String) reqParam.get("name");
			entityList = (List<String>) reqParam.get("entity");
			filterList = (List<String>) reqParam.get("fq");

			if (monitorName == null || monitorName.isEmpty() || entityList == null || entityList.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("Insufficient Arguments");
			}
			EnterprisePref enterprisePref = requestParsingService.getDefaultSpec();
			MonitorAPIResponse apiRes = monitorService.createMonitor(Long.parseLong(UserInfoThreadLocal.get().getUserId()), monitorName,
					entityList, filterList, enterprisePref);
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in createmonitor api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				res = responseDecoratorService.getMonitorWrapperResponse(apiRes, "create.monitor.succ");
			} else {
				throw new Exception("createmonitor api returned null.");
			}
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "createmonitor");
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

	@SuppressWarnings({"rawtypes"})
	@RequestMapping(value = "/{id}/remove", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse remove(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String monitorId) {
		MonitorWrapperResponse res = null;
		int errorCode = -1;
		try {
			long monitorIdL = -1;
			try {
				monitorIdL = Long.parseLong(monitorId.split(COLON)[1]);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				LOG.warn("Invalid monitorId passed in the url: " + monitorId);
				throw new Exception();
			}

			MonitorAPIResponse apiRes = monitorService.removeMonitor(UserInfoThreadLocal.get(), monitorIdL);
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in createmonitor api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null) {
				res = responseDecoratorService.getMonitorWrapperResponse(apiRes, "remove.monitor.succ");
			} else {
				throw new Exception("createmonitor api returned null.");
			}
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "removemonitor");
			request.setAttribute("targetId", monitorId);
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
	@RequestMapping(value = "/{id}/addEntity", method = {RequestMethod.PUT,
			RequestMethod.POST}, headers = "Accept=application/json")
	public JSONResponse addEntity(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody,
			@PathVariable("id") String monitorId) {
		return processOperation(OperationType.ADD_ENTITY, request, reqBody, monitorId);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/removeEntity", method = {RequestMethod.PUT,
			RequestMethod.POST}, headers = "Accept=application/json")
	public JSONResponse removeEntity(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String reqBody,
			@PathVariable("id") String monitorId) {
		return processOperation(OperationType.REMOVE_ENTITY, request, reqBody, monitorId);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/addFilter", method = {RequestMethod.PUT,
			RequestMethod.POST}, headers = "Accept=application/json")
	public JSONResponse addFilter(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody,
			@PathVariable("id") String monitorId) {
		return processOperation(OperationType.ADD_FILTER, request, reqBody, monitorId);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/removeFilter", method = {RequestMethod.PUT,
			RequestMethod.POST}, headers = "Accept=application/json")
	public JSONResponse removeFilter(HttpServletRequest request, HttpServletResponse response,
			@RequestBody String reqBody,
			@PathVariable("id") String monitorId) {
		return processOperation(OperationType.REMOVE_FILTER, request, reqBody, monitorId);
	}

	@SuppressWarnings("unchecked")
	private JSONResponse processOperation(OperationType operationType, HttpServletRequest request, String reqBody,
			String monitorIdParam) {
		String monitorId = monitorIdParam;
		MonitorInfoResponse res = new MonitorInfoResponse();
		int errorCode = -1;
		try {
			Map<String, List<String>> reqParam;
			List<String> operationsLst;

			monitorId = monitorId.split(COLON)[1]; // U:113
			long frmonitorId;
			try {
				frmonitorId = Long.parseLong(monitorId);
			} catch (NumberFormatException e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			if (StringUtils.isEmpty(reqBody)) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}

			try {
				reqParam = JSONUtility.deserialize(reqBody, Map.class);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}

			operationsLst = reqParam.get(operationType.getRequestParam());
			if (CollectionUtils.isEmpty(operationsLst)) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("Insufficient Arguments");
			}

            res = operationType.executeOperation(this, frmonitorId, operationsLst);
			// Usagetracking
			MonitorControllerHelper.getrackingRequestParams(request, "M:" + monitorId, reqBody);
		} catch (ControllerOperationException e) {
			int errCode = Integer.parseInt(e.getMessage().split(COLON)[1].trim());
			LOG.error(e.getMessage(), e);
			return requestParsingService.getErrorResponse(errCode);
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
	@RequestMapping(value = "/{monitorId}/queryEntity", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse queryEntity(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("monitorId") String monitorId,
			@RequestParam(value = "entity", required = true) String entity) {
		EntityResponse res;
		int errorCode = -1;
		try {
			// Usagetracking
			MonitorControllerHelper.getrackingRequestParams(request, monitorId, "{\"entity\":\"" + entity + "\"}");
			long frmonitorId = -1;
			try {
				frmonitorId = Long.parseLong(monitorId.split(COLON)[1]);
			} catch (NumberFormatException e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}

			MonitorAPIResponse apiRes = monitorService.getEntityStatus(UserInfoThreadLocal.get(), frmonitorId, entity);
			if (apiRes != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in getUserById api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null && apiRes.getEntityStatus() != null) {
				res = responseDecoratorService.getMonitorEntityResponse(apiRes.getEntityStatus());
			} else {
				if (apiRes != null) {
					errorCode = apiRes.getStatusCode();
				}
				throw new Exception("getEntitiesStatus api returned null, errorCode: " + errorCode);
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

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{monitoId}/config", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse get(HttpServletRequest request, HttpServletResponse response, @PathVariable("monitoId") String monitoIdParam) {
		String monitoId = monitoIdParam;
		MonitorConfigResponse res = null;
		int errorCode = -1;
		try {
			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("target", "monitorconfig");

			monitoId = monitoId.split(COLON)[1]; // U:113
			long frmonitoId = -1;
			try {
				frmonitoId = Long.parseLong(monitoId);
				request.setAttribute("targetId", "M:" + monitoId);
			} catch (NumberFormatException e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			MonitorAPIResponse apiRes = monitorService.getMonitorDetails(UserInfoThreadLocal.get(), frmonitoId);
			if (apiRes != null && apiRes.getMonitorConfig() != null && apiRes.getStatusCode() != 200) {
				errorCode = apiRes.getStatusCode();
				throw new Exception("Error in getMonitorDetails api , error code: " + apiRes.getStatusCode());
			} else if (apiRes != null && apiRes.getMonitorConfig() != null) {
				res = responseDecoratorService.getMonitorConfigResponse(apiRes.getMonitorConfig());
			} else {
				if (apiRes != null) {
					errorCode = apiRes.getStatusCode();
				}
				throw new Exception("getMonitorDetails api returned null, errorCode: " + errorCode);
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

    MonitorService getMonitorService() {
        return monitorService;
    }

    RequestParsingService getRequestParsingService() {
        return requestParsingService;
    }

    ResponseDecoratorService getResponseDecoratorService() {
        return responseDecoratorService;
    }
}
