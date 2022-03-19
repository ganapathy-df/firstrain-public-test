package com.firstrain.web.controller.core;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.firstrain.frapi.pojo.NotableDetails;
import com.firstrain.frapi.service.TwitterService;
import com.firstrain.web.response.JSONResponse;
import com.firstrain.web.service.core.Constant;
import com.firstrain.web.service.core.FreemarkerTemplateService;
import com.firstrain.web.service.core.HttpClientService;
import com.firstrain.web.service.core.RequestParsingService;

@Controller
@RequestMapping(value = "/common")
public class CommonController {

	private static final Logger LOG = Logger.getLogger(EntityController.class);
	@Autowired
	private FreemarkerTemplateService ftlService;
	@Autowired
	private RequestParsingService requestParsingService;
	@Autowired
	private TwitterService twitterService;
	@Autowired
	private HttpClientService httpClientService;

	@RequestMapping(value = "/tweetNotable", method = RequestMethod.GET)
	public JSONResponse<String> getNotableDetails(HttpServletRequest req, @RequestParam(value = "tweetId", required = true) long tweetId,
			@RequestParam(value = "groupId", required = false) long groupId,
			@RequestParam(value = "sectionId", required = false) String sectionId,
			@RequestParam(value = "viewId", required = false) String viewId,
			@RequestParam(value = "metaData", required = false, defaultValue = "") String metaData) {

		JSONResponse<String> res = new JSONResponse<String>();
		try {
			Map<String, Object> ftlParams = new HashMap<String, Object>();
			ftlParams.put("reqScheme", requestParsingService.getRequestScheme(req));
			ftlParams.put("appName", Constant.getAppName());
			ftlParams.put("imgCssURL", Constant.getImgCssURL());
			if (sectionId != null) {
				ftlParams.put("sectionId", sectionId);
			} else {
				ftlParams.put("sectionId", viewId);
			}
			ftlParams.put("viewId", viewId);
			ftlParams.put("tweetId", tweetId);
			ftlParams.put("metadata", metaData);

			NotableDetails notableDetails = twitterService.getNotableDetails(tweetId, groupId, false);
			ftlParams.put("data", notableDetails);
			String html = ftlService.getHtml("tweet-notable-tooltip.ftl", ftlParams);
			res.setResult(html);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return requestParsingService.getErrorResponse(-1);
		}
		return res;
	}

	@RequestMapping(value = "/image", method = RequestMethod.GET)
	public void image(@RequestParam("u") String imgURL, HttpServletResponse res) {
		try {
			byte[] bytes = httpClientService.getData(imgURL);
			if (imgURL.toLowerCase().endsWith(".png")) {
				res.setContentType("image/png");
			} else if (imgURL.toLowerCase().endsWith(".gif")) {
				res.setContentType("image/gif");
			} else {
				res.setContentType("image/jpg");
			}
			res.getOutputStream().write(bytes);
			res.getOutputStream().flush();
		} catch (Exception e) {
			LOG.error("Error in proxy image req : " + imgURL, e);
		}
	}
}
