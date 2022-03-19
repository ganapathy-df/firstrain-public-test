/**
 * 
 */
package com.firstrain.web.controller.staticdata;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.util.StatusCode;
import com.firstrain.web.response.EntityDataResponse;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.staticdata.Constant;
import com.firstrain.web.service.staticdata.FreemarkerTemplateService;
import com.firstrain.web.service.staticdata.RequestParsingService;
import com.firstrain.web.service.staticdata.StaticDataService;

/**
 * @author gkhanchi
 *
 */
@Controller
@RequestMapping(value = "/entity")
public class EntityController {
	private static final Logger LOG = Logger.getLogger(EntityController.class);
	private static final String VIEW_FTL = "entity-brief.ftl";
	private static final String SECTION_TYPE_E = "E";
	private static final String SECTION_TYPE_TE = "TE";
	private static final String SECTION_TYPE_WV = "WV";
	private static final String SECTION_TYPE_FR = "FR";
	private static final String SECTION_TYPE_FT = "FT";
	private static final String SECTION_TYPE_TT = "TT";
	private static final String SECTION_TYPE_BI = "BI";
	private static final String SECTION_TYPE_MD = "MD";
	private static final String SECTION_TYPE_TWT = "TWT";
	private static final String SECTION_TYPE_GL = "GL";
	private static final String SECTION_TYPE_RL = "RL";
	

	@Autowired
	private FreemarkerTemplateService ftlService;
	@Autowired
	private StaticDataService staticDataService;
	@Autowired
	private RequestParsingService requestParsingService;
	@Value("${app.base.url}")
	private String appBaseUrl;

	private Map<SectionType, SectionSpec> getSectionsMap(int errorCodeParam, String sections) 
			throws ExecutionException {
		int errorCode = errorCodeParam;
		Map<SectionType, SectionSpec> sectionsMap = null;
		if (sections != null) {
			try {
				sectionsMap = requestParsingService.getSectionsPageSpecMap(sections);
			} catch (Exception e) {
				errorCode = StatusCode.ILLEGAL_ARGUMENT;
				throw new ExecutionException("Error while parsing parameter - <sections> : " + e.getMessage(), e);
			}
		} else {
			LOG.warn("Parameter <sections> not passed in the url.");
			errorCode = StatusCode.INSUFFICIENT_ARGUMENT;
			throw new ExecutionException("Error as parameter - <sections> not passed in url", new Exception());
		}
		return sectionsMap;
	}
	
	private static boolean validateKeySetForHtml(Set<SectionType> keySet) {
		return !keySet.contains(SectionType.valueOf(SECTION_TYPE_E)) 
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_TE)) 
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_WV));
	}
	
	private void validateKeySet(Set<SectionType> keySet, int errorCode, String sections) 
			throws ExecutionException {
		validateKeySet(keySet, errorCode, sections, true);
	}
	
	private void validateKeySet(Set<SectionType> keySet, int errorCodeParam, String sections, boolean otherValidatedKeySet) 
			throws ExecutionException {
		int errorCode = errorCodeParam;
		if (otherValidatedKeySet && !keySet.contains(SectionType.valueOf(SECTION_TYPE_FR)) 
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_FT)) 
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_TT)) 
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_BI))
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_MD)) 
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_TWT))
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_GL)) 
				&& !keySet.contains(SectionType.valueOf(SECTION_TYPE_RL))) {
			errorCode = StatusCode.ILLEGAL_ARGUMENT;
			throw new ExecutionException("Invalid value passed in parameter <sections> : " + sections, new Exception());
		}
	}
	
	@RequestMapping(value = "/{id}/brief", method = RequestMethod.GET)
	public JSONResponse getResponseInAllFormat(HttpServletRequest request, HttpServletResponse response,
			@PathVariable("id") String entityId, @RequestParam(value = "sections", required = true) String sections,
			@RequestParam(value = "fq", required = false) String fq,
			@RequestParam(value = "htmlFrag", required = false, defaultValue = "classicNoFrame") String htmlFrag,
			@RequestParam(value = "results", required = false) String resultsCSV) {
		JSONResponse res = new JSONResponse();
		int errorCode = -1;
		try {
			Map<SectionType, SectionSpec> sectionsMap = getSectionsMap(errorCode, sections);
			EntityDataResponse obj = staticDataService.getDataObject("entitybrief.txt", EntityDataResponse.class);

			// to handle static data only
			if (sectionsMap != null) {
				Set<SectionType> keySet = sectionsMap.keySet();
				validateKeySet(keySet, errorCode, sections);
			}
			if (obj != null) {
				if (resultsCSV == null) {
					obj.getResult().setMetaData(null);
					obj.getResult().setHtmlFrag(null);
				} else {
					if (!resultsCSV.contains("M")) {
						obj.getResult().setMetaData(null);
					}
					if (!resultsCSV.contains("H")) {
						obj.getResult().setHtmlFrag(null);
					}
					for (String type : resultsCSV.split(",")) {
						if (type.equals("H")) {
							Map<String, Object> ftlParams = new HashMap<String, Object>();
							ftlParams.put("obj", obj);
							if (htmlFrag.equalsIgnoreCase("classicFrame")) {
								ftlParams.put("showheading", "true");
							} else {
								ftlParams.put("showheading", "false");
							}
							obj.getResult().getHtmlFrag().setFr(ftlService.getHtml("documents.ftl", ftlParams));
							ftlParams.put("reqScheme", requestParsingService.getRequestScheme(request));
							obj.getResult().getHtmlFrag().setFt(ftlService.getHtml("tweets.ftl", ftlParams));
						}
					}
					if (!resultsCSV.contains("D")) {
						obj.getResult().setData(null);
					}
				}
				res.setMessage("Data populated successfully.");
				res.setResult(obj.getResult());
				res.setStatus(ResStatus.SUCCESS);
			}

			// Usagetracking
			populateAttributes(request, entityId); 
			
			String metadata = requestParsingService.getSerializedMetadata(sections, null, fq, resultsCSV, htmlFrag);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}
		} catch (Exception e) {
			if (errorCode < 0) {
				LOG.error(e.getMessage(), e);
			}
			return requestParsingService.getErrorResponse(errorCode);
		}
		return res;
	}

	@RequestMapping(value = "/{id}/brief", method = RequestMethod.GET, headers = "Accept=text/html")
	public String getResponseInHtml(Model model, HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "sections", required = true) String sections,
			@RequestParam(value = "layout", required = false) String layout,
			@RequestParam(value = "htmlFrag", required = false, defaultValue = "classicNoFrame") String htmlFrag,
			@RequestParam(value = "fq", required = false) String fq, @PathVariable("id") String entityId) {
		int errorCode = -1;
		try {
			Map<SectionType, SectionSpec> sectionsMap = getSectionsMap(errorCode, sections);
			EntityDataResponse obj = staticDataService.getDataObject("entitybrief.txt", EntityDataResponse.class);
			Map<String, Object> ftlParams = new HashMap<String, Object>();
			// to handle static data only
			if (sectionsMap != null) {
				Set<SectionType> keySet = sectionsMap.keySet();
				boolean validatedhtmlSpecficKeySet = validateKeySetForHtml(keySet);
				validateKeySet(keySet, errorCode, sections, validatedhtmlSpecficKeySet);
				
				if (keySet.contains(SectionType.valueOf("FR"))) {
					ftlParams.put("showFR", true);
				}
				if (keySet.contains(SectionType.valueOf("FT"))) {
					ftlParams.put("showFT", true);
				}
				if (keySet.contains(SectionType.valueOf("E"))) {
					ftlParams.put("showE", true);
				}
				if (keySet.contains(SectionType.valueOf("TE"))) {
					ftlParams.put("showTE", true);
				}
				if (keySet.contains(SectionType.valueOf("WV"))) {
					ftlParams.put("showWV", true);
				}
				ftlParams.put("imgCssURL", Constant.getImgCssURL());
				ftlParams.put("jsURL", Constant.getJsURL());
				ftlParams.put("appName", Constant.getAppName());
				ftlParams.put("version", Constant.getVersion());
				requestParsingService.handleSectionsForStaticData(keySet, obj, "C:72239");
				requestParsingService.setChartDataForHtml(keySet, obj, "C:72239");
			}

			ftlParams.put("obj", obj);
			if (htmlFrag.equalsIgnoreCase("classicFrame")) {
				ftlParams.put("showheading", "true");
			} else {
				ftlParams.put("showheading", "false");
			}
			ftlParams.put("reqScheme", requestParsingService.getRequestScheme(request));
			String view = VIEW_FTL;
			if (layout != null) {
				view = "entity-brief-" + layout + ".ftl";
			}
			String html = ftlService.getHtml(view, ftlParams);
			model.addAttribute("htmlContent", html);

			// Usagetracking
			populateAttributes(request, entityId); 
			
			String metadata = requestParsingService.getSerializedMetadata(sections, null, fq, null, htmlFrag);
			if (metadata != null && !metadata.isEmpty()) {
				request.setAttribute("metaData", metadata);
			}
			request.setAttribute("str1", "Accept=text/html");
		} catch (Exception e) {
			if (errorCode < 0) {
				LOG.error(e.getMessage(), e);
			}
			// model.addAttribute("errorMsg", requestParsingService.getErrorHtmlResponse(errorCode));
			model.addAttribute("errorMsg", requestParsingService.getErrorHtmlResponse(errorCode));
		}
		return "view";
	}
 
	private void populateAttributes(final HttpServletRequest request, final String entityId) { 
		request.setAttribute("loadview", true); 
		request.setAttribute("activityType", request.getMethod()); 
		request.setAttribute("target", "entitybrief"); 
		request.setAttribute("targetId", entityId); 
	} 
	
}
