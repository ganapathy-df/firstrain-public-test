package com.firstrain.web.controller.core;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.frapi.domain.VisualizationData;
import com.firstrain.frapi.domain.VisualizationData.ChartType;
import com.firstrain.frapi.service.VisualizationService;
import com.firstrain.utils.JSONUtility;
import com.firstrain.web.controller.BaseWidgetController;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;

@Controller
@RequestMapping(value = "/widget")
public class WidgetController extends BaseWidgetController {

	private static final Logger LOG = Logger.getLogger(WidgetController.class);

	@Autowired
	private VisualizationService visualizationService;
	
	
	@Value("${js.base.url}")
	private String jsURL;

	@RequestMapping(value = "/graph", method = RequestMethod.GET)
	public void getGraph(HttpServletResponse response, 
			@RequestParam("callback") String callback, 
			@RequestParam("id") String id,
			@RequestParam(value = "fq", required = false) String fq, 
			@RequestParam(value = "chTypes",
				defaultValue = "TREE_MONITOR_SEARCH,TREE_COMPANY,TREE_TOPICS,ACC_METER,GEO_WORLD,GEO_US") String chTypes) {

		response.setContentType("text/javascript; charset=UTF-8");
		JSONResponse<Object> res = new JSONResponse<Object>();
		try {

			List<ChartType> chTypeList = new ArrayList<ChartType>();
			String[] chartTypeArr = chTypes.split(",");
			for (String ch : chartTypeArr) {
				chTypeList.add(ChartType.valueOf(ch));
			}
			VisualizationData vd = null;
			if (id.startsWith("M:")) {
				long tagId = Long.parseLong(id.substring(2));
				vd = visualizationService.getVisualizationByMonitorId(tagId, 12, chTypeList, fq, true, true);
			} else {
				vd = visualizationService.getVisualizationByEntityToken(id, 12, chTypeList, fq, true, true);
			}
			res.setResult(vd);
			res.setStatus(ResStatus.SUCCESS);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			res.setStatus(ResStatus.ERROR);
		}
		try {
			PrintWriter out = response.getWriter();
			out.print(callback + "(" + JSONUtility.serialize(res) + ")");
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
