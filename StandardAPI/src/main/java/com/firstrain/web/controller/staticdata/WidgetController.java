package com.firstrain.web.controller.staticdata;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.utils.JSONUtility;
import com.firstrain.web.controller.BaseWidgetController;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.response.JSONResponse.ResStatus;
import com.firstrain.web.service.staticdata.StaticDataService;

@Controller
@RequestMapping(value = "/widget")
public class WidgetController extends BaseWidgetController {

	private static final Logger LOG = Logger.getLogger(WidgetController.class);

	@Autowired
	private StaticDataService staticDataService;

	
	@Value("${js.base.url}")
	private String jsURL;
	
	@RequestMapping(value = "/graph", method = RequestMethod.GET)
	public void getGraph(HttpServletResponse response, 
			@RequestParam("callback") String callback, 
			@RequestParam("id") String id,
			@RequestParam(value = "chTypes", 
				defaultValue = "TREE_MONITOR_SEARCH,TREE_COMPANY,TREE_TOPICS,ACC_METER,GEO_WORLD,GEO_US") String chTypes) {

		response.setContentType("text/javascript; charset=UTF-8");
		JSONResponse<Object> res = new JSONResponse<Object>();
		try {
			Object o = null;
			if (id.startsWith("M:")) {
				o = staticDataService.getDataObject("visualization.txt", Object.class);
			} else {
				o = staticDataService.getDataObject("visualization_entity.txt", Object.class);
			}
			res.setResult(((Map) o).get("data"));
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
