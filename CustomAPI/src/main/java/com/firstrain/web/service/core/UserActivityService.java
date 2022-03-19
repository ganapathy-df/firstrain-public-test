package com.firstrain.web.service.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.web.pojo.UserActivity;

@Service
public class UserActivityService {
	private static final Logger LOG = Logger.getLogger(UserActivityService.class);
	private static final Logger PERF_LOG = Logger.getLogger("PerfLogging");

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	@Qualifier("httpClient")
	@Autowired
	private HttpClient httpClient;

	public void addActivity(final UserActivity userActivity) {

		taskExecutor.submit(new Runnable() {
			@Override
			public void run() {
				if (Constant.getUserActivityServiceURL() == null || Constant.getUserActivityServiceURL().isEmpty()) {
					return;
				}
				PerfMonitor.startRequest("U:" + userActivity.getUserId(), "activityTrack");
				try {
					Map<String, String> params = createParamMap(userActivity);
					populateParamMapAndPostData(params, userActivity);
				} catch (Exception e) {
					LOG.error("Error in UserActivity service for: " + userActivity, e);
				} finally {
					PERF_LOG.info(PerfMonitor.endRequest().toStringLog());
				}
			}
		});
	}

	private Map<String, String> createParamMap(final UserActivity userActivity) {
	    Map<String, String> params = new HashMap<String, String>();
	    params.put("userId", Integer.toString(userActivity.getUserId()));
	    params.put("dnbUserId", userActivity.getDnbUserId());
	    params.put("activityType", userActivity.getActivity());
	    params.put("channel", userActivity.getChannel());
	    params.put("activityTime", userActivity.getActivityTime().toString());
	    params.put("view", userActivity.getView());
	    params.put("viewId", userActivity.getViewId());
	    params.put("section", userActivity.getSection());
	    params.put("sectionId", userActivity.getSectionId());
	    params.put("subSection", userActivity.getSubSection());
	    params.put("subSectionId", userActivity.getSubSectionId());
	    return params;
	}

	private void populateParamMapAndPostData(final Map<String, String> params, final UserActivity userActivity) throws Exception {
	    if (userActivity.getTarget() != null) {
	    	params.put("target", userActivity.getTarget());
	    } else {
	    	params.put("target", getTarget(userActivity.getTargetId()));
	    }
	    params.put("targetId", userActivity.getTargetId());
	    params.put("enterpriseId", Integer.toString(userActivity.getEnterpriseId()));
	    params.put("responseTime", Integer.toString(userActivity.getResponseTime()));
	    params.put("userAgent", userActivity.getUserAgent());
	    params.put("destination", userActivity.getDestination());
	    params.put("metaData", userActivity.getMetaData());
	    params.put("userip", userActivity.getUserip());
	    params.put("str1", userActivity.getStr1());
	    params.put("str2", userActivity.getStr2());
	    params.put("apiVersion", userActivity.getApiVersion());
	    
	    postData(params);
	}

	private final String getTarget(final String targetId) {
		if (targetId == null) {
			return "default";
		}
		if (targetId.startsWith("D:") || targetId.startsWith("SEC:")) {
			return "document";
		} else if (targetId.startsWith("TW:")) {
			return "tweet";
		} else if (targetId.startsWith("E:") || targetId.startsWith("SECE:") || targetId.startsWith("MT:")) {
			return "event";
		} else if (targetId.startsWith("T:")) {
			return "topic";
		} else if (targetId.startsWith("C:")) {
			return "company";
		} else if (targetId.startsWith("S:")) {
			return "search";
		} else if (targetId.startsWith("M:")) {
			return "monitor";
		} else if (targetId.startsWith("U:")) {
			return "user";
		} else if (targetId.startsWith("R:")) {
			return "region";
		} else if (targetId.startsWith("I:")) {
			return "industry";
		} else if (targetId.startsWith("B:")) {
			return "businesslines";
		} else if (targetId.startsWith("C-")) {
			return "company";
		} else if (targetId.startsWith("T-")) {
			return "topic";
		}
		return "default";
	}

	private final void postData(Map<String, String> params) throws Exception {
		PostMethod method = null;
		long st = PerfMonitor.currentTime();
		try {
			method = new PostMethod(Constant.getUserActivityServiceURL() + "/useractivity/addUserActivity");
			method.addRequestHeader(new Header("Accept", "application/json"));
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (entry.getValue() == null || entry.getValue().isEmpty()) {
					continue;
				}
				if (entry.getKey().equalsIgnoreCase("metaData") && entry.getValue() != null) {
					method.addParameter(entry.getKey(), entry.getValue());
				} else {
					nameValuePairs.add(new NameValuePair(entry.getKey(), entry.getValue()));
				}
			}
			NameValuePair[] nameValuePairsArr = new NameValuePair[nameValuePairs.size()];
			method.setQueryString(nameValuePairs.toArray(nameValuePairsArr));
			httpClient.executeMethod(method);
			LOG.info("Add User Activity: " + method.getURI());
		} catch (Exception e) {
			if (method != null) {
				LOG.error("Error while getting data for: " + method.getURI() + " :: " + method.getQueryString(), e);
			} else {
				LOG.error(e.getMessage(), e);
			}
		} finally {
			if (method != null) {
				method.releaseConnection();
			}
			PerfMonitor.recordStats(st, PerfActivityType.Rest, "act.log.post");
		}
	}

}
