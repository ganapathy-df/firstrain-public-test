/**
 * 
 */
package com.firstrain.web.controller.core;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.firstrain.frapi.domain.MgmtChart;
import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.EnterprisePref;
import com.firstrain.frapi.pojo.EntityBriefInfo;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;
import com.firstrain.frapi.service.EntityBriefService;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.core.RequestParsingService;
import com.firstrain.web.service.core.ResponseDecoratorService;


/**
 * @author gkhanchi
 *
 */
@Controller
@RequestMapping(value = "/chart")
public class ChartsController {
	private static final Logger LOG = Logger.getLogger(ChartsController.class);
	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private ResponseDecoratorService responseDecoratorService;
	@Autowired
	private EntityBriefService entityBaseService;

	

	@RequestMapping(value = "/wvchartdata", method = RequestMethod.GET)
	public @ResponseBody String getWVChartData(HttpServletResponse response, @RequestParam("q") String searchToken) {
		int errorCode = -1;
		String xml = null;
		try {
			EnterprisePref enterprisePref = requestParsingService.getDefaultSpec(false, false);
			Map<SectionType, SectionSpec> sectionsMap = new LinkedHashMap<SectionType, SectionSpec>();
			enterprisePref.setSectionsMap(sectionsMap);
			sectionsMap.put(SectionType.WV, new SectionSpec());
			EntityBriefInfo entityBriefInfo = entityBaseService.getEntityBriefDetails(enterprisePref, searchToken, null);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in entitybrief api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null && entityBriefInfo.getWebVolumeGraph() != null) {
				xml = entityBriefInfo.getWebVolumeGraph().getGraphXml();
			} else {
				throw new Exception("wvchartdata api returned null.");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return requestParsingService.getErrorResponse(errorCode).getMessage();
		}
		return xml;
	}

	@RequestMapping(value = "/mtchartdata", method = RequestMethod.GET)
	public JSONResponse<MgmtChart> getMTChartData(HttpServletResponse response, @RequestParam("q") String searchToken) {
		int errorCode = -1;
		JSONResponse<MgmtChart> res = new JSONResponse<MgmtChart>();
		try {
			EnterprisePref enterprisePref = requestParsingService.getDefaultSpec(false, false);
			Map<SectionType, SectionSpec> sectionsMap = new LinkedHashMap<SectionType, SectionSpec>();
			enterprisePref.setSectionsMap(sectionsMap);
			sectionsMap.put(SectionType.MTC, new SectionSpec());
			EntityBriefInfo entityBriefInfo = entityBaseService.getEntityBriefDetails(enterprisePref, searchToken, null);
			if (entityBriefInfo != null && entityBriefInfo.getStatusCode() != 200) {
				errorCode = entityBriefInfo.getStatusCode();
				throw new Exception("Error in entitybrief api , error code: " + entityBriefInfo.getStatusCode());
			} else if (entityBriefInfo != null && entityBriefInfo.getMgmtTurnoverData() != null) {
				res.setResult(entityBriefInfo.getMgmtTurnoverData().getMgmtRGraphChart());
				res.setStatus(ResStatus.SUCCESS);
			} else {
				throw new Exception("mtchartdata api returned null.");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return requestParsingService.getErrorResponse(errorCode);
		}
		return res;
	}
}
