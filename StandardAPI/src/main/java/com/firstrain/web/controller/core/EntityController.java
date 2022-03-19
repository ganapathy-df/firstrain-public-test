package com.firstrain.web.controller.core;

import com.firstrain.frapi.domain.EntityMap;
import com.firstrain.frapi.domain.Parameter;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.Entity;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.SearchAPIResponse;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.pojo.wrapper.GetBulk;
import com.firstrain.frapi.service.CompanyService;
import com.firstrain.frapi.service.DnbService;
import com.firstrain.frapi.service.EntityBriefService;
import com.firstrain.frapi.service.SearchService;
import com.firstrain.frapi.util.DefaultEnums.CoversationStarterType;
import com.firstrain.frapi.util.DefaultEnums.INPUT_ENTITY_TYPE;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.solr.client.AutoCompleteHelperService;
import com.firstrain.solr.client.SearchSpec;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.helper.MonitorControllerHelper;
import com.firstrain.web.pojo.ConversationStartersInputBean;
import com.firstrain.web.pojo.EntityDataHtml;
import com.firstrain.web.pojo.EntityMatchInputBean;
import com.firstrain.web.pojo.GetPeersInputBean;
import com.firstrain.web.pojo.Tweet;
import com.firstrain.web.response.DnBEntityStatusResponse;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.ExcelProcessingHelperService;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;
import com.firstrain.web.service.core.StorageService;
import com.firstrain.web.util.UserInfoThreadLocal;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping(value = "/entity")
public class EntityController {

	private static final Logger log = Logger.getLogger(EntityController.class);
	private static final String VIEW_FTL = "entity-brief.ftl";
	private static final Short MT_MAX_COUNT = 500;
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
	private static final String SHOW_HEADING = "showheading";
	private static final String ENTITY_BRIEF = "entitybrief";
	private static final String TARGET = "target";

	@Autowired
	ExcelProcessingHelperService excelProcessingHelperService;
	@Autowired
	private FreemarkerTemplateService ftlService;
	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private EntityBriefService entityBaseService;
	@Autowired
	private DnbService dnbService;
	@Autowired
	private SearchService searchService;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private StorageService storageService;

	

	@Value("${mt.lastndays:10}")
	private Integer lastNDays;

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/brief", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse getResponseInAllFormat(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") String searchToken, @RequestParam(value = "sections", required = true) String sections,
			@RequestParam(value = "fq", required = false) String fq, @RequestParam(value = "debug", required = false) boolean debug,
			@RequestParam(value = "htmlFrag", required = false, defaultValue = "classicNoFrame") String htmlFrag,
			@RequestParam(value = "results", required = false, defaultValue = "D") String resultsCSV,
			@RequestParam(value = "tpUserInfo", required = false) String tpUserInfo,
			@RequestParam(value = "scopeDirective", required = false) String scopeDirective,
			@RequestParam(value = "needPhrases", required = false, defaultValue = "false") boolean needPhrases,
			@RequestParam(value = "entitylinks", required = false, defaultValue = "false") boolean entityLinking,
			@RequestParam(value = "blendDUNS", required = false, defaultValue = "false") boolean blendDUNS) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			EnterprisePref enterprisePref;
			Map<SectionType, SectionSpec> sectionsMap;
			Set<SectionType> keySet;
			boolean isNotExcludeTweetFields;

			log.debug("Processing Entity Brief API For Json, Input Params ->  searchToken : " + searchToken + ", sections : " + sections
					+ ", fq : " + fq + ", debug : " + debug + ", htmlFrag : " + htmlFrag + ", results : " + resultsCSV + ", tpUserInfo : "
					+ tpUserInfo + ", needPhrases : " + needPhrases + ", entitylinks : " + entityLinking);
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
			// fetch visualization for results type H also to check and add content availability
			// flag with
			// visualization web widgets
			sectionsMap.put(SectionType.VIZ, null);
			// for result type JSON - attach related document metadata with events
			if (resultsCSV.contains("D") || resultsCSV.contains("d")) {
				SectionSpec sectionSpec = sectionsMap.get(SectionType.E);
				if (sectionSpec != null) {
					sectionSpec.setNeedRelatedDoc(true);
				}
				sectionSpec = sectionsMap.get(SectionType.WV);
				if (sectionSpec != null) {
					sectionSpec.setNeedRelatedDoc(true);
				}
				// for json format return nodelist even if single mode is present
				enterprisePref.setApplyMinNodeCheckInVisualization(false);
			}

			EntityBriefInfo entityBriefInfo = entityBaseService.getEntityBriefDetails(enterprisePref, searchToken, fq, scopeDirective,
					blendDUNS);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in entitybrief api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null) {

				isNotExcludeTweetFields = responseDecoratorService.excludeTweetInfo(UserInfoThreadLocal.get().getOwnedBy());
				boolean excludeTweetInfo = (StringUtils.isNotEmpty(resultsCSV) && (resultsCSV.contains("H") || resultsCSV.contains("h"))) ? false
						: isNotExcludeTweetFields;
				if (needPhrases || entityLinking) {

					List<String> ids = new ArrayList<String>();
					List<String> wrIds = storageService.getListOfIdsfromDocumentSet(entityBriefInfo.getWebResults());
					if (CollectionUtils.isNotEmpty(wrIds)) {
						ids.addAll(wrIds);
					}
					List<String> acIds = storageService.getListOfIdsfromDocumentSet(entityBriefInfo.getAnalystComments());
					if (CollectionUtils.isNotEmpty(acIds)) {
						ids.addAll(acIds);
					}
					Map<String, GetBulk> map = storageService.getDocFieldsFromStorageService(ids, needPhrases, entityLinking);

					if (MapUtils.isNotEmpty(map)) {
						storageService.populateFieldInDocSet(map, entityBriefInfo.getWebResults());
						storageService.populateFieldInDocSet(map, entityBriefInfo.getAnalystComments());
					}

				}
				entityDataResponse = responseDecoratorService.getEntityDataResponse(entityBriefInfo, "gen.succ", sectionsMap, response,
						enterprisePref.getIndustryClassificationId(), excludeTweetInfo);


			} else {
				throw new Exception("createmonitor api returned null.");
			}

			if (resultsCSV != null && !resultsCSV.isEmpty() && entityDataResponse != null) {
				final MonitorControllerHelper helper = new MonitorControllerHelper(-1);
				helper.setFtlService(ftlService);
				helper.setRequestParsingService(requestParsingService);
				helper.setResponseDecoratorService(responseDecoratorService);
				helper.updateEntityResponseData(request, response, entityBriefInfo.getEntity().getSearchToken(), fq, debug, htmlFrag, resultsCSV,
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

			boolean isPagination = false;
			if (sections != null && sections.contains("io:")) {
				isPagination = true;
			}

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("targetId", searchToken);
			if (resultsCSV != null && !resultsCSV.isEmpty()) {
				request.setAttribute(TARGET, "entitybriefwithmultipleresponsetype");
			} else if (fq != null && !fq.isEmpty()) {
				request.setAttribute(TARGET, "entitybriefwithfilteringsupport");
				if (isPagination) {
					request.setAttribute(TARGET, "entitybriefwithpagingandfilteringsupport");
				}
			} else if (isPagination) {
				request.setAttribute(TARGET, "entitybriefwithpagingsupport");
			} else {
				request.setAttribute(TARGET, ENTITY_BRIEF);
			}
			String metadata =
					requestParsingService.getSerializedMetadata(sections, null, fq, resultsCSV, htmlFrag, null, needPhrases, entityLinking,
							blendDUNS);
			retrieveAndSetMetaDataAttribute(metadata, tpUserInfo, request);
		} catch (Exception e) {
			if (errorCode > 0) {
				log.warn(e.getMessage());
			} else {
				log.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return entityDataResponse;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/te", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse getMTResponseInAllFormat(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") String searchToken, @RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "count", required = false, defaultValue = "500") Short count,
			@RequestParam(value = "fq", required = false) String fq, @RequestParam(value = "debug", required = false) boolean debug,
			@RequestParam(value = "htmlFrag", required = false, defaultValue = "classicNoFrame") String htmlFrag,
			@RequestParam(value = "results", required = false, defaultValue = "D") String resultsCSV,
			@RequestParam(value = "tpUserInfo", required = false) String tpUserInfo) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {

			String sections = "{te}";
			EnterprisePref enterprisePref = requestParsingService.getSectionsPageSpecMap(sections);
			Map<SectionType, SectionSpec> sectionsMap = enterprisePref.getSectionsMap();
			Set<SectionType> keySet = null;

			final MonitorControllerHelper helper = new MonitorControllerHelper(-1);
			keySet = helper.validateSectionsAndUpdateRequest(sectionsMap, request ,requestParsingService, sections);
			String[] strArr = new String[3];
			try {
				getValues(startDate, endDate, count, strArr);
			} catch (Exception ex) {
				errorCode = StatusCode.INVALID_TIMEZONE;
				log.warn("dates are not in range of 5 days, startDate : " + startDate + ", endDate : " + endDate);
				throw new Exception();
			}

			EntityBriefInfo entityBriefInfo =
					entityBaseService.getEntityBriefDetailsForMT(enterprisePref, searchToken, fq, strArr[0], strArr[1], strArr[2]);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in entitybrief api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null) {
				entityDataResponse = responseDecoratorService.getEntityDataResponse(entityBriefInfo, "gen.succ", sectionsMap, response,
						enterprisePref.getIndustryClassificationId(), true);
			} else {
				throw new Exception("createmonitor api returned null.");
			}

			if (resultsCSV != null && !resultsCSV.isEmpty() && entityDataResponse != null) {

				if (resultsCSV.contains("H") || resultsCSV.contains("h")) {
					Map<String, Object> ftlParams = new HashMap<String, Object>();
					populateObjAndShowHeading(ftlParams, entityDataResponse, htmlFrag); 
					
					entityDataResponse.getResult().setHtmlFrag(new EntityDataHtml());
					responseDecoratorService.setChartDataForHtml(keySet, entityDataResponse, entityBriefInfo.getEntity().getSearchToken(),
							sectionsMap, debug, fq, response);
				}

				if (!resultsCSV.contains("M") && !resultsCSV.contains("m")) {
					// if do not populate metadata then set it to null
					entityDataResponse.getResult().setMetaData(null);
				}
				if (!resultsCSV.contains("D") && !resultsCSV.contains("d")) {
					// if do not populate data then set it to null
					entityDataResponse.getResult().setData(null);
				}
			} else {
				// if no results then always populated metadata set to null
				entityDataResponse.getResult().setMetaData(null);
			}

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("targetId", searchToken);
			if (resultsCSV != null && !resultsCSV.isEmpty()) {
				request.setAttribute(TARGET, "entitybriefwithmultipleresponsetype");
			} else if (fq != null && !fq.isEmpty()) {
				request.setAttribute(TARGET, "entitybriefwithfilteringsupport");
			} else {
				request.setAttribute(TARGET, ENTITY_BRIEF);
			}
			String metadata = requestParsingService.getSerializedMetadata(sections, null, fq, resultsCSV, htmlFrag);
			retrieveAndSetMetaDataAttribute(metadata, tpUserInfo, request);
		} catch (Exception e) {
			if (errorCode > 0) {
				log.warn(e.getMessage());
			} else {
				log.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return entityDataResponse;
	}

	private void retrieveAndSetMetaDataAttribute(String metadataParam, final String tpUserInfo, final HttpServletRequest request) {
		String metadata = metadataParam;
		metadata = retrieveMetaData(metadata, tpUserInfo); 
		
		
		if (metadata != null && !metadata.isEmpty()) {
			request.setAttribute("metaData", metadata);
		}
	}

	@RequestMapping(value = "/{id}/te", method = RequestMethod.GET)
	public String getMTResponseInHtml(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate,
			@RequestParam(value = "count", required = false, defaultValue = "500") Short count,
			@RequestParam(value = "layout", required = false) String layout,
			@RequestParam(value = "htmlFrag", required = false, defaultValue = "classicNoFrame") String htmlFrag,
			@RequestParam(value = "fq", required = false) String fq, @RequestParam(value = "debug", required = false) boolean debug,
			@PathVariable("id") String searchToken, @RequestParam(value = "tpUserInfo", required = false) String tpUserInfo) {
		int errorCode = -1;
		try {
			String sections = "{te}";
			EnterprisePref enterprisePref = requestParsingService.getSectionsPageSpecMap(sections, true, true);
			Map<SectionType, SectionSpec> sectionsMap = enterprisePref.getSectionsMap();
			if (enterprisePref.getCustomizedCssFileName() != null) {
				model.addAttribute("cssfilename", enterprisePref.getCustomizedCssFileName());
			}
			Set<SectionType> keySet = null;

			Map<String, Object> ftlParams = new HashMap<String, Object>();
			if (sectionsMap != null) {
				final MonitorControllerHelper helper = new MonitorControllerHelper(-1);
				keySet = helper.validateSectionsAndUpdateRequest(sectionsMap, request ,requestParsingService, sections);
				if (keySet.contains(SectionType.valueOf("TE"))) {
					ftlParams.put("showTE", true);
				}

				ftlParams.put("imgCssURL", Constant.getImgCssURL());
				ftlParams.put("jsURL", Constant.getJsURL());
				ftlParams.put("appName", Constant.getAppName());
				ftlParams.put("version", Constant.getVersion());
				ftlParams.put("str1", request.getHeader("Accept"));
			}

			String metadata = requestParsingService.getSerializedMetadata(sections, null, fq, null, htmlFrag, searchToken);
			populateMetaDataViewIdAndToken(metadata, tpUserInfo, request, ftlParams, searchToken);

			String[] strArr = new String[3];
			try {
				getValues(startDate, endDate, count, strArr);
			} catch (Exception ex) {
				errorCode = StatusCode.INVALID_TIMEZONE;
				log.warn("dates are not in range of 5 days, startDate : " + startDate + ", endDate : " + endDate);
				throw new Exception();
			}

			EntityDataResponse entityDataResponse = null;
			EntityBriefInfo entityBriefInfo =
					entityBaseService.getEntityBriefDetailsForMT(enterprisePref, searchToken, fq, strArr[0], strArr[1], strArr[2]);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in entitybrief api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null) {
				entityDataResponse = responseDecoratorService.getEntityDataResponse(entityBriefInfo, "gen.succ", sectionsMap, response,
						enterprisePref.getIndustryClassificationId(), true);
				if (layout != null) {
					responseDecoratorService.setChartDataForAnalyticsRibbonHtml(keySet, entityDataResponse,
							entityBriefInfo.getEntity().getSearchToken(), sectionsMap, debug, fq);
				} else {
					responseDecoratorService.setChartDataForHtml(keySet, entityDataResponse, entityBriefInfo.getEntity().getSearchToken(),
							sectionsMap, debug, fq, response);
				}
				/*
				 * if(keySet.contains(SectionType.valueOf("TE"))) { //mgmt chart plugin
				 * requestParsingService.setChartDataForMTChart(entityDataResponse, searchToken, null, debug); }
				 */
			} else {
				throw new Exception("entity brief api returned null.");
			}

			setFtlParamsAndRequestAttributes(model, request, layout, htmlFrag, fq, searchToken, sections, ftlParams,
					entityDataResponse, entityBriefInfo);
		} catch (Exception e) {
			if (errorCode < 0) {
				log.error(e.getMessage(), e);
			}
			model.addAttribute("errorMsg", requestParsingService.getErrorHtmlResponse(errorCode));
		}
		return "view";
	}

	@RequestMapping(value = "/{id}/brief", method = RequestMethod.GET)
	public String getResponseInHtml(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "sections", required = true) String sections, @RequestParam(value = "layout", required = false) String layout,
			@RequestParam(value = "htmlFrag", required = false, defaultValue = "classicNoFrame") String htmlFrag,
			@RequestParam(value = "fq", required = false) String fq, @RequestParam(value = "debug", required = false) boolean debug,
			@PathVariable("id") String searchToken, @RequestParam(value = "tpUserInfo", required = false) String tpUserInfo,
			@RequestParam(value = "blendDUNS", required = false, defaultValue = "false") boolean blendDUNS) {
		int errorCode = -1;
		try {
			EnterprisePref enterprisePref = null;
			Map<SectionType, SectionSpec> sectionsMap = null;
			Set<SectionType> keySet = null;

			log.debug("Processing Entity Brief API For Json, Input Params ->  searchToken : " + searchToken + ", sections : " + sections
					+ ", fq : " + fq + ", debug : " + debug + ", htmlFrag : " + htmlFrag + ", tpUserInfo : " + tpUserInfo + ", layout : " + layout);

			if (sections != null) {
				try {
					enterprisePref = requestParsingService.getSectionsPageSpecMap(sections, true, true);
					sectionsMap = enterprisePref.getSectionsMap();
					// set customized css file name
					if (enterprisePref.getCustomizedCssFileName() != null) {
						model.addAttribute("cssfilename", enterprisePref.getCustomizedCssFileName());
					}
				} catch (Exception e) {
					log.warn("Error parsing parameter <sections>: " + sections + e.getMessage());
					errorCode = StatusCode.INVALID_SECTION;
					throw e;
				}
			} else {
				log.warn("Parameter <sections> not passed in the url.");
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception();
			}
			Map<String, Object> ftlParams = new HashMap<String, Object>();
			if (sectionsMap != null) {
				final MonitorControllerHelper helper = new MonitorControllerHelper(-1);
				keySet = helper.validateSectionsAndUpdateRequest(sectionsMap, request ,requestParsingService, sections,
						ftlParams);
				if (keySet.contains(SectionType.valueOf("FR"))) {
					ftlParams.put("showFR", true);
				}
				if (keySet.contains(SectionType.valueOf("FT"))) {
					ftlParams.put("showFT", true);
					ftlParams.put("showNotable", true);
					// tweet accelerometer is not required for layout page
					if (layout == null) {
						SectionSpec ftSpec = sectionsMap.get(SectionType.valueOf("FT"));
						ftSpec.setNeedTweetAccelerometer(true);
					}
				}
				if (keySet.contains(SectionType.valueOf("TE"))) {
					ftlParams.put("showTE", true);
				}
				if (keySet.contains(SectionType.valueOf("E"))) {
					ftlParams.put("showE", true);
				}
				if (keySet.contains(SectionType.valueOf("AC"))) {
					ftlParams.put("showAC", true);
				}

				ftlParams.put("imgCssURL", Constant.getImgCssURL());
				ftlParams.put("jsURL", Constant.getJsURL());
				ftlParams.put("appName", Constant.getAppName());
				ftlParams.put("version", Constant.getVersion());
				ftlParams.put("str1", request.getHeader("Accept"));
			}

			String metadata = requestParsingService.getSerializedMetadata(sections, null, fq, null, htmlFrag, searchToken, false, false,
					blendDUNS);
			populateMetaDataViewIdAndToken(metadata, tpUserInfo, request, ftlParams, searchToken);

			// fetch visualization to check and add content availability response headers for
			// visualization web widgets
			sectionsMap.put(SectionType.VIZ, null);

			EntityDataResponse entityDataResponse = null;
			EntityBriefInfo entityBriefInfo = entityBaseService.getEntityBriefDetails(enterprisePref, searchToken, fq, null, blendDUNS);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in entitybrief api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null) {
				entityDataResponse = responseDecoratorService.getEntityDataResponse(entityBriefInfo, "gen.succ", sectionsMap, response,
						enterprisePref.getIndustryClassificationId(), false);
				if (layout != null) {
					responseDecoratorService.setChartDataForAnalyticsRibbonHtml(keySet, entityDataResponse,
							entityBriefInfo.getEntity().getSearchToken(), sectionsMap, debug, fq);
				} else {
					responseDecoratorService.setChartDataForHtml(keySet, entityDataResponse, entityBriefInfo.getEntity().getSearchToken(),
							sectionsMap, debug, fq, response);
				}
				if (keySet.contains(SectionType.valueOf("TE"))) {
					// mgmt chart plugin
					requestParsingService.setChartDataForMTChart(entityDataResponse, searchToken, null, debug);
				}
				if (keySet.contains(SectionType.valueOf("WV"))) {
					SectionSpec wvSpec = sectionsMap.get(SectionType.valueOf("WV"));
					// add plugin
					responseDecoratorService.setChartDataForWebVolume(entityDataResponse, searchToken, wvSpec, debug, response);
				}
			} else {
				throw new Exception("entity brief api returned null.");
			}

			setFtlParamsAndRequestAttributes(model, request, layout, htmlFrag, fq, searchToken, sections, ftlParams,
					entityDataResponse, entityBriefInfo);
		} catch (Exception e) {
			if (errorCode < 0) {
				log.error(e.getMessage(), e);
			}
			model.addAttribute("errorMsg", requestParsingService.getErrorHtmlResponse(errorCode));
		}
		return "view";
	}

	private void populateMetaDataViewIdAndToken(String metadataParam, final String tpUserInfo, final HttpServletRequest request, final Map<String, Object> ftlParams, final String searchToken) {
		String metadata = metadataParam;
		metadata = retrieveMetaData(metadata, tpUserInfo); 
		
		
		if (metadata != null && !metadata.isEmpty()) {
			request.setAttribute("metaData", metadata);
			ftlParams.put("metadata", metadata);
		}
		ftlParams.put("viewId", searchToken);
		// used in load more - handle dnbid case
		ftlParams.put("token", searchToken);
	}
 
	private String retrieveMetaData(String metadataParam, final String tpUserInfo) { 
		String metadata = metadataParam;
		if (metadata != null && !metadata.isEmpty()) { 
			if (tpUserInfo != null && !tpUserInfo.isEmpty()) { 
				metadata = metadata.substring(0, metadata.length() - 1) + tpUserInfo.replaceFirst("\\{", ","); 
			} 
		} else if (tpUserInfo != null && !tpUserInfo.isEmpty()) { 
			metadata = tpUserInfo; 
		} 
		return metadata; 
	} 
	

	private void setFtlParamsAndRequestAttributes(Model model, HttpServletRequest request, String layout,
			String htmlFrag, String fq, String searchToken, String sections, Map<String, Object> ftlParams,
			EntityDataResponse entityDataResponse, EntityBriefInfo entityBriefInfo)
			throws IOException, TemplateException {
		populateParams(entityDataResponse, fq, ftlParams, htmlFrag);
		ftlParams.put("imgCssURL", Constant.getImgCssURL());
		ftlParams.put("reqScheme", requestParsingService.getRequestScheme(request));
		String activityView = populatePaginationCountGetActivityView(ftlParams);
		String view = VIEW_FTL;
		if (layout != null) {
			view = "entity-brief-" + layout + ".ftl";
			activityView = "entitybrieflayout";
			// to handle cross section if duns is provided in the input
			ftlParams.put("searchToken", entityBriefInfo.getEntity().getSearchToken());
		}
		String html = ftlService.getHtml(view, ftlParams);
		model.addAttribute("htmlContent", html);

		populateRequestAttributes(activityView, fq, request, searchToken, sections);
	}

	private void populateRequestAttributes(final String activityView, final String fq, final HttpServletRequest request, final String searchToken, final String sections) {
	    String target = ENTITY_BRIEF;
	    request.setAttribute("loadview", true);
	    request.setAttribute("activityType", "loadview");
	    request.setAttribute("targetId", searchToken);
	    request.setAttribute("view", activityView);
	    request.setAttribute("viewId", searchToken);
	    boolean isPagination = false;
	    if (StringUtils.isNotEmpty(sections)) {
	    	isPagination = sections.contains("io:");
	    }
	    if (StringUtils.isNotEmpty(fq)) {
	    	target = "entitybriefwithfilteringsupport";
	    	if (isPagination) {
	    		target = "entitybriefwithpagingandfilteringsupport";
	    	}
	    } else if (isPagination) {
	    	target = "entitybriefwithpagingsupport";
	    }
	    request.setAttribute(TARGET, target);
	}

	private void populateParams(final EntityDataResponse entityDataResponse, final String fq, final Map<String, Object> ftlParams, final String htmlFrag) {
	    populateObjAndShowHeading(ftlParams, entityDataResponse, htmlFrag); 
	    
	    ftlParams.put("fq", fq);
	    ftlParams.put("showmore", true);
	}
 
	private void populateObjAndShowHeading(final Map<String, Object> ftlParams, final EntityDataResponse entityDataResponse, final String htmlFrag) { 
		ftlParams.put("obj", entityDataResponse); 
		if (htmlFrag.equalsIgnoreCase("classicFrame")) { 
			ftlParams.put(SHOW_HEADING, "true"); 
		} else { 
			ftlParams.put(SHOW_HEADING, "false"); 
		} 
	} 
	

	private String populatePaginationCountGetActivityView(final Map<String, Object> ftlParams) {
	    populatePaginationCountParams(ftlParams); 
	    
	    
	    String activityView = ENTITY_BRIEF;
	    return activityView;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/match", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse getEntityMatch(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}
			EntityMatchInputBean entityMatchInputBean = null;
			try {
				entityMatchInputBean = JSONUtility.deserialize(reqBody, EntityMatchInputBean.class);
				entityMatchInputBean.setCount(AutoCompleteHelperService.DEF_AUTO_SUGGEST_COUNT);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			String q = null;
			EntityBriefInfo entityBriefInfo = null;
			INPUT_ENTITY_TYPE type = null;
			if (entityMatchInputBean.getCompany() != null) {
				try {
					q = requestParsingService.getQForEntityMatch(entityMatchInputBean);
					type = INPUT_ENTITY_TYPE.COMPANY;
				} catch (Exception e) {
					errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
					throw e;
				}
				entityBriefInfo = companyService.getCompanyAutoSuggestList(q, entityMatchInputBean);
			} else if (entityMatchInputBean.getTopic() != null || entityMatchInputBean.getIndustry() != null
					|| entityMatchInputBean.getRegion() != null) {
				if (entityMatchInputBean.getTopic() != null) {
					q = entityMatchInputBean.getTopic().getName();
					type = INPUT_ENTITY_TYPE.TOPIC;
				} else if (entityMatchInputBean.getIndustry() != null) {
					q = entityMatchInputBean.getIndustry().getName();
					type = INPUT_ENTITY_TYPE.INDUSTRY;
				} else if (entityMatchInputBean.getRegion() != null) {
					q = entityMatchInputBean.getRegion().getName();
					type = INPUT_ENTITY_TYPE.REGION;
				}
				entityBriefInfo = entityBaseService.getEntityMatch(q, entityMatchInputBean.getCount(), type,
						(entityMatchInputBean.getTopic() != null ? "2,3" : null));
			}

			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in companyService.getCompanyAutoSuggestList api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null) {
				entityDataResponse = responseDecoratorService.getMatchedEntityDataResponse(entityBriefInfo, type);
			} else {
				throw new Exception("companyService.getCompanyAutoSuggestList api returned null.");
			}
			// Usagetracking
			populateAttributes(entityMatchInputBean, q, request);
		} catch (Exception e) {
			return retrieveErrorResponse(e, errorCode);
		}
		return entityDataResponse;
	}

	private void populateAttributes(final EntityMatchInputBean entityMatchInputBean, final String q, final HttpServletRequest request) {
	    request.setAttribute("loadview", true);
	    request.setAttribute("activityType", request.getMethod());
	    if (q != null && !q.isEmpty()) {
	    	request.setAttribute("targetId", q);
	    } else {
	    	request.setAttribute("targetId", entityMatchInputBean.getCompany().getName());
	    }
	    request.setAttribute(TARGET, "entityMatch");
	}

	private JSONResponse retrieveErrorResponse(final Exception e, final int errorCode) {
	    if (errorCode > 0) {
	    	log.warn(e.getMessage());
	    } else {
	    	log.error(e.getMessage(), e);
	    }
	    return requestParsingService.getErrorResponse(errorCode);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/conversationStarters", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse getCSResponse(HttpServletRequest request, HttpServletResponse response, @RequestBody String reqBody,
			@RequestParam(value = "io", required = false, defaultValue = "0") int start,
			@RequestParam(value = "ic", required = false, defaultValue = "20") int count) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			if (reqBody == null || reqBody.isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}
			ConversationStartersInputBean conversationStartersInputBean = null;
			try {
				conversationStartersInputBean = JSONUtility.deserialize(reqBody, ConversationStartersInputBean.class);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw e;
			}
			// to be used for tagging with documents
			Map<String, CoversationStarterType> coversationStarterMap = new HashMap<String, CoversationStarterType>();
			String[] qMulti = null;
			try {
				qMulti = requestParsingService.getQMultiFromReqBody(conversationStartersInputBean, coversationStarterMap);
			} catch (Exception e) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw e;
			}
			if (qMulti != null) {
				Parameter parameter = requestParsingService.getParameterForMultiSearch(qMulti.length);
				SearchAPIResponse searchAPIResponse = getSearchAPIResponse(parameter, qMulti);
				if (searchAPIResponse != null && searchAPIResponse.getStatusCode() != 200) {
					errorCode = searchAPIResponse.getStatusCode();
					throw new Exception("Error in searchService.getMultiSectionWebResults api , error code: " + searchAPIResponse.getStatusCode());
				} else if (searchAPIResponse != null) {
					entityDataResponse = responseDecoratorService.getConversationStartersResponse(searchAPIResponse,
							conversationStartersInputBean.getLeadCompany(), coversationStarterMap, start, count);
				} else {
					throw new Exception("searchService.getMultiSectionWebResults api returned null.");
				}
				// Usagetracking
				populateAttributes(conversationStartersInputBean, request);
			}
		} catch (Exception e) {
			return getErrorResponse(e, errorCode);
		}
		return entityDataResponse;
	}

	private void populateAttributes(final ConversationStartersInputBean conversationStartersInputBean, final HttpServletRequest request) throws IOException {
	    request.setAttribute("loadview", true);
	    request.setAttribute("activityType", request.getMethod());
	    if (conversationStartersInputBean.getLeadCompany() != null) {
	    	request.setAttribute("targetId", conversationStartersInputBean.getLeadCompany().get("token"));
	    } else if (conversationStartersInputBean.getLead() != null) {
	    	request.setAttribute("targetId", conversationStartersInputBean.getLead().get("name"));
	    }
	    request.setAttribute(TARGET, "conversationStarters");
	    if (conversationStartersInputBean.getTpUserInfo() != null) {
	    	request.setAttribute("metaData", JSONUtility.serialize(conversationStartersInputBean.getTpUserInfo()));
	    }
	}

	private SearchAPIResponse getSearchAPIResponse(final Parameter parameter, final String[] qMulti) throws Exception {
	    int[] scopeMulti = new int[qMulti.length];
	    for (int i = 0; i < qMulti.length; i++) {
	    	scopeMulti[i] = SearchSpec.SCOPE_NARROW;
	    }
	    EnterprisePref enterprisePref = new EnterprisePref();
	    enterprisePref.setEnterpriseId(UserInfoThreadLocal.get().getOwnedBy());
	    
	    SearchAPIResponse searchAPIResponse = searchService.getMultiSectionWebResults(qMulti, null, null, parameter, enterprisePref);
	    return searchAPIResponse;
	}

	private JSONResponse getErrorResponse(final Exception e, final int errorCode) {
	    if (errorCode > 0) {
	    	log.warn(e.getMessage());
	    } else {
	    	log.error(e.getMessage(), e);
	    }
	    return requestParsingService.getErrorResponse(errorCode);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/status", method = RequestMethod.GET)
	public JSONResponse getDnBEntityStatus(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String dnbIds) {
		int errorCode = -1;
		DnBEntityStatusResponse entityDataResponse = null;
		try {
			List<Entity> entityList = null;
			try {
				entityList = dnbService.getDnbEntities(dnbIds);
			} catch (Exception e) {
				log.warn("Error parsing parameter <dnbIds>: " + dnbIds + e.getMessage());
				errorCode = StatusCode.ENTITY_NOT_FOUND;
				throw e;
			}
			if (entityList != null) {
				entityDataResponse = responseDecoratorService.getDnBEntityStatusResponse(entityList);
			} else {
				throw new Exception("getDnbEntities api returned null.");
			}

			// Usagetracking
			request.setAttribute("loadview", true);
			request.setAttribute("activityType", request.getMethod());
			request.setAttribute("targetId", dnbIds);
			request.setAttribute(TARGET, "dnbentitystatus");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return requestParsingService.getErrorResponse(errorCode);
		}
		return entityDataResponse;
	}

	@RequestMapping(value = "loadMore", method = RequestMethod.GET)
	public JSONResponse<String> loadMore(HttpServletRequest req, HttpServletResponse response, @RequestParam("q") String searchToken,
			@RequestParam(value = "layout", required = false) String layout,
			@RequestParam(value = "lasteventdate", required = false) String lastEventDate, @RequestParam("sectiontype") String sectionType,
			@RequestParam(value = "scopeDirective", required = false) String scopeDirective,
			@RequestParam("start") short start, @RequestParam("count") short count, @RequestParam(value = "fq", required = false) String fq) {
		JSONResponse<String> res = new JSONResponse<String>();
		int errorCode = -1;

		try {
			EnterprisePref enterprisePref = requestParsingService.getDefaultSpec(true, false);
			Map<SectionType, SectionSpec> sectionsMap = new HashMap<SectionType, SectionSpec>();
			SectionSpec ss = new SectionSpec();
			ss.setNeedPagination(true);
			ss.setCount(count);
			ss.setStart(start);

			Map<String, Object> ftlParams = new HashMap<String, Object>();

			SectionType stype = SectionType.valueOf(sectionType);
			if (stype.equals(SectionType.E)) {
				ss.setNeedBucket(true);
				ftlParams.put("lasteventdate", lastEventDate);
			}
			if (stype.equals(SectionType.FT)) {
				ftlParams.put("showNotable", true);
			}
			sectionsMap.put(stype, ss);
			enterprisePref.setSectionsMap(sectionsMap);

			ftlParams.put("reqScheme", requestParsingService.getRequestScheme(req));
			ftlParams.put("imgCssURL", Constant.getImgCssURL());
			ftlParams.put("fq", fq);
			// used in load more - handle dnbid case
			ftlParams.put("token", searchToken);
			ftlParams.put(sectionType, true);
			populatePaginationCountParams(ftlParams); 
			

			EntityDataResponse entityDataResponse = null;
			EntityBriefInfo entityBriefInfo = StringUtils.isEmpty(scopeDirective)
					? entityBaseService.getEntityBriefDetails(enterprisePref, searchToken, fq)
					: entityBaseService.getEntityBriefDetails(enterprisePref, searchToken, fq, scopeDirective, Boolean.TRUE);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in entitybrief api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null) {
				entityDataResponse = responseDecoratorService.getEntityDataResponse(entityBriefInfo, "gen.succ", sectionsMap, response,
						enterprisePref.getIndustryClassificationId(), false);
			} else {
				throw new Exception("entity brief api returned null.");
			}
			ftlParams.put("obj", entityDataResponse);

			String view = "load-more.ftl";
			if ("classicBrief".equals(layout)) {
				view = "load-more-ly.ftl";
			}
			res.setResult(ftlService.getHtml(view, ftlParams));
		} catch (Exception e) {
			if (errorCode < 0) {
				log.error(e.getMessage(), e);
			}
			res.setResult(requestParsingService.getErrorHtmlResponse(errorCode));
		}
		return res;
	}
 
	private void populatePaginationCountParams(final Map<String, Object> ftlParams) { 
		ftlParams.put("fr_paginationcount", Constant.FR_PAGINATION_COUNT); 
		ftlParams.put("ft_paginationcount", Constant.FT_PAGINATION_COUNT); 
		ftlParams.put("ac_paginationcount", Constant.AC_PAGINATION_COUNT); 
		ftlParams.put("te_paginationcount", Constant.TE_PAGINATION_COUNT); 
		ftlParams.put("e_paginationcount", Constant.E_PAGINATION_COUNT); 
	} 
	

	@RequestMapping(value = "crossSection", method = RequestMethod.GET)
	public JSONResponse<String> crossSection(HttpServletRequest req, @RequestParam("q1") String q1, @RequestParam("q2") String q2,
			@RequestParam("name") String name, @RequestParam(value = "fq", required = false) String fq) {
		JSONResponse<String> res = new JSONResponse<String>();
		int errorCode = -1;

		try {
			EnterprisePref enterprisePref = requestParsingService.getDefaultSpec();

			Map<String, Object> ftlParams = new HashMap<String, Object>();
			ftlParams.put("reqScheme", requestParsingService.getRequestScheme(req));
			ftlParams.put("imgCssURL", Constant.getImgCssURL());
			ftlParams.put("fq", fq);
			ftlParams.put("name1", name);
			ftlParams.put("name2", q2);

			EntityDataResponse entityDataResponse = null;
			String qMulti[] = new String[2];
			qMulti[0] = q1 + " AND " + q2;
			qMulti[1] = q2;

			ftlParams.put("q1", qMulti[0]);
			ftlParams.put("q2", qMulti[1]);

			SearchAPIResponse searchAPIResponse = searchService.getMultiSectionWebResults(qMulti, null, fq, null, enterprisePref);
			if (searchAPIResponse != null && searchAPIResponse.getStatusCode() != 200) {
				errorCode = searchAPIResponse.getStatusCode();
				throw new Exception("Error in dosumentSet api , error code: " + searchAPIResponse.getStatusCode());
			} else if (searchAPIResponse != null) {
				entityDataResponse =
						responseDecoratorService.getEntityDataResponse(searchAPIResponse, "gen.succ", enterprisePref.getSectionsMap(), ftlParams);
			} else {
				throw new Exception("entity brief api returned null.");
			}
			ftlParams.put("obj", entityDataResponse);
			res.setResult(ftlService.getHtml("cross-section.ftl", ftlParams));
		} catch (Exception e) {
			if (errorCode < 0) {
				log.error(e.getMessage(), e);
			}
			res.setResult(requestParsingService.getErrorHtmlResponse(errorCode));
		}
		return res;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/map", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse getEntityMap(HttpServletRequest request, @PathVariable("id") String searchToken) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			if (searchToken == null || searchToken.trim().isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}
			EntityBriefInfo entityBriefInfo = entityBaseService.getEntityMap(searchToken);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in entity map api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null) {
				entityDataResponse = responseDecoratorService.getEntityMapResponse(entityBriefInfo, "gen.succ");
			} else {
				throw new Exception("createmonitor api returned null.");
			}
			// Usagetracking
			populateAttributes(request, searchToken);
		} catch (Exception e) {
			return getError(e, errorCode);
		}
		return entityDataResponse;
	}

	private void populateAttributes(final HttpServletRequest request, final String searchToken) {
	    populateAttributes(request, "entitymap", searchToken);
	}

	private JSONResponse getError(final Exception e, final int errorCode) {
	    if (errorCode > 0) {
	    	log.warn(e.getMessage());
	    } else {
	    	log.error(e.getMessage(), e);
	    }
	    return requestParsingService.getErrorResponse(errorCode);
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/getPeers", method = RequestMethod.POST, headers = "Accept=application/json")
	public JSONResponse getEntityPeers(HttpServletRequest request, @PathVariable("id") String searchToken, @RequestBody String reqBody) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			if (searchToken == null || searchToken.trim().isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("reqBody is null");
			}

			GetPeersInputBean getPeersInputBean = null;

			if (reqBody != null && !reqBody.trim().isEmpty()) {
				try {
					getPeersInputBean = JSONUtility.deserialize(reqBody, GetPeersInputBean.class);
				} catch (Exception e) {
					errorCode = StatusCode.ILLEGAL_ARGUMENT;
					throw e;
				}
			}

			List<String> searchTokenList = null;
			if (getPeersInputBean != null) {
				searchTokenList = getPeersInputBean.getSearchTokenList();
			}

			EntityBriefInfo entityBriefInfo = entityBaseService.getEntityPeers(searchToken, searchTokenList, true);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in getPeers api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null) {
				entityDataResponse = responseDecoratorService.getEntityPeersResponse(entityBriefInfo, "gen.succ");
			} else {
				throw new Exception("getPeers api returned null.");
			}
			// Usagetracking
			populateAttributes(request, "entitypeers", searchToken);
		} catch (Exception e) {
			if (errorCode > 0) {
				log.warn(e.getMessage());
			} else {
				log.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return entityDataResponse;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/metaData", method = RequestMethod.GET, headers = "Accept=application/json")
	public JSONResponse metaData(HttpServletRequest request, @PathVariable("id") String searchToken) {
		EntityDataResponse entityDataResponse = null;
		int errorCode = -1;
		try {
			if (searchToken == null || searchToken.trim().isEmpty()) {
				errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
				throw new Exception("searchToken is null");
			}
			String language = excelProcessingHelperService.getSourceMetaData(searchToken);
			EntityMap entityMap = entityBaseService.getEntityMapBySourceSearchToken(searchToken, language);
			if (entityMap != null) {
				EntityBriefInfo entityBriefInfo = new EntityBriefInfo();
				entityBriefInfo.setEntityMap(entityMap);
				entityDataResponse = responseDecoratorService.getEntityMapResponse(entityBriefInfo, "gen.succ");
			} else {
				errorCode = StatusCode.ENTITY_NOT_FOUND;
				throw new Exception("Error in source metadata api , no mapping found for token: " + searchToken);
			}
			// Usagetracking
			getrackingPopulateRequest(request, searchToken);
		} catch (Exception e) {
			if (errorCode > 0) {
				log.warn(e.getMessage());
			} else {
				log.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return entityDataResponse;
	}

	private void getrackingPopulateRequest(HttpServletRequest request, @PathVariable("id") String searchToken) {
		populateAttributes(request, "sourceMetaData", searchToken);
	}

	private void populateAttributes(final HttpServletRequest request, final String targetValue, final String searchToken) {
		request.setAttribute("loadview", true);
		request.setAttribute("activityType", request.getMethod());
		request.setAttribute(TARGET, targetValue);
		request.setAttribute("targetId", searchToken);
		request.setAttribute("viewId", searchToken);
	}

	private void getValues(String startDate, String endDate, Short count, String[] strArr) throws Exception {

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(new Date());
		cal2.add(Calendar.DATE, -lastNDays);

		String from = null;
		String to = null;

		// 1. start and end both null
		// 2. start null
		// 3. end null
		// 4. both present

		if (StringUtils.isNotEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
			// 4. both present

			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(startDate));

			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(sdf.parse(endDate));

			long diff = cal1.getTimeInMillis() - cal.getTimeInMillis();
			long dateDiff = TimeUnit.MILLISECONDS.toDays(diff);

			// int dateDiff = cal1.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH);
			if (dateDiff > -1 && dateDiff < lastNDays) {
				from = sdf1.format(cal.getTime());
				to = sdf1.format(cal1.getTime());
			} else {
				throw new Exception();
			}

		} else if (StringUtils.isNotEmpty(startDate) && StringUtils.isEmpty(endDate)) {
			// 3. startDate present and end date null
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(startDate));
			from = sdf1.format(cal.getTime());

			cal.add(Calendar.DATE, +lastNDays);
			to = sdf1.format(cal.getTime());

		} else if (StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
			// 2. stardDate null and endDate present
			Calendar cal = Calendar.getInstance();
			cal.setTime(sdf.parse(endDate));
			to = sdf1.format(cal.getTime());

			cal.add(Calendar.DATE, -lastNDays);
			from = sdf1.format(cal.getTime());

		} else {
			// 1. startDate and endDate both null
			from = sdf1.format(cal2.getTime());
			to = "*";
		}

		populateFromToAndCount(count, from, strArr, to);
	}

	private void populateFromToAndCount(Short countParam, final String from, final String[] strArr, final String to) {
	    Short count = countParam;
	    if (count > MT_MAX_COUNT) {
	    	count = MT_MAX_COUNT;
	    }
	    
	    strArr[0] = from;
	    strArr[1] = to;
	    strArr[2] = count.toString();
	}

	public static void main(String[] args) throws ParseException {
		String startDate = "2015-08-28";
		String endDate = "2015-09-02";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(startDate));

		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(sdf.parse(endDate));



		// Get msec from each, and subtract.
		long diff = cal1.getTimeInMillis() - cal.getTimeInMillis();
		long days = TimeUnit.MILLISECONDS.toDays(diff);
		System.out.println("Days : " + days);
		/*
		 * int months = (cal.get(Calendar.YEAR) - cal1.get(Calendar.YEAR)) * 12 + (cal.get(Calendar.MONTH)- cal1.get(Calendar.MONTH)) +
		 * (cal.get(Calendar.DAY_OF_MONTH) >= cal1.get(Calendar.DAY_OF_MONTH)? 0: -1); System.out.println("Diff is.." + months);
		 * 
		 * 
		 * System.out.println(cal.get(Calendar.DAY_OF_MONTH)); System.out.println(cal1.get(Calendar.DAY_OF_MONTH));
		 * System.out.println(cal1.get(Calendar.DAY_OF_MONTH) - cal.get(Calendar.DAY_OF_MONTH)); int dateDiff = cal1.get(Calendar.DAY_OF_MONTH)
		 * - cal.get(Calendar.DAY_OF_MONTH); if (dateDiff > -1 && dateDiff < 6) { System.out.println( "Dates in range"); } else {
		 * System.out.println( "Dates are not in range"); }
		 * 
		 * 
		 */
		/*
		 * Calendar cal2 = Calendar.getInstance(); cal2.setTime(new Date()); cal2.add(Calendar.DATE, -5);
		 * 
		 * //cal > cal2 && cal1 > cal2 System.out.println(cal.compareTo(cal2)); System.out.println(cal1.compareTo(cal2)); if
		 * ((cal.compareTo(cal2) <= 0) || (cal1.compareTo(cal2) <= 0)) { System.out.println("date not in range"); } else { System.out.println(
		 * "date in range"); }
		 */
	}
}
