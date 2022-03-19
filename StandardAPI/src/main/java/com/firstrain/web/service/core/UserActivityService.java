package com.firstrain.web.service.core;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.firstrain.frapi.domain.User;
import com.firstrain.frapi.pojo.AuthAPIResponse;
import com.firstrain.utils.object.PerfActivityType;
import com.firstrain.utils.object.PerfMonitor;
import com.firstrain.web.pojo.UserActivity;

@Service
public class UserActivityService {
	private static final Logger LOG = Logger.getLogger(UserActivityService.class);
	private static final Logger PERF_LOG = Logger.getLogger("PerfLogging");
	private static final String NL = String.format("%n");

	@Autowired
	private ThreadPoolTaskExecutor taskExecutor;
	@Qualifier("httpClient")
	@Autowired
	private HttpClient httpClient;

	public void addActivity(final User user, final AuthAPIResponse authAPIResponse, final Map<String, String[]> reqParams,
			final Timestamp activityTime, final int responseTime, final String userAgent, final String userIP, final String appName,
			final String version) {

		taskExecutor.submit(new Runnable() {
			@Override
			public void run() {
				if (Constant.getUserActivityServiceURL() == null || Constant.getUserActivityServiceURL().isEmpty()) {
					return;
				}
				PerfMonitor.startRequest(user.getUserId(), "activityTrack");
				try {
					Map<String, String> params = new HashMap<String, String>();
					String userId = user.getUserId();
					params.put("userId", userId);
					params.put("activityType", getParamValue("activityType", reqParams));
					params.put("channel", appName);
					params.put("activityTime", activityTime.toString());
					params.put("view", getParamValue("activityView", reqParams));
					if (authAPIResponse != null) {
						params.put("enterpriseId", Long.toString(authAPIResponse.getEnterpriseId()));
						params.put("str2", authAPIResponse.getAuthKey());
					}
					params.put("responseTime", Integer.toString(responseTime));
					params.put("userAgent", userAgent);
					params.put("destination", getParamValue("destination", reqParams));
					params.put("metaData", getParamValue("metaData", reqParams));
					params.put("userip", userIP);
					params.put("apiVersion", version);
					params.put("str1", getParamValue("str1", reqParams));
					String viewId = getParamValue("viewId", reqParams);
					if (viewId != null) {
						params.put("viewId", viewId);
					} else {
						params.put("viewId", "U:" + userId);
					}

					String section = getParamValue("section", reqParams);
					if (section != null) {
						params.put("section", section);
					} else if (getParamValue("sectionId", reqParams) != null) {
						params.put("section", getTarget(getParamValue("sectionId", reqParams)));
					}
					params.put("sectionId", getParamValue("sectionId", reqParams));

					String subSection = getParamValue("subSection", reqParams);
					if (subSection != null) {
						params.put("subSection", subSection);
					} else if (getParamValue("subSectionId", reqParams) != null) {
						params.put("subSection", getTarget(getParamValue("subSectionId", reqParams)));
					}
					params.put("subSectionId", getParamValue("subSectionId", reqParams));

					String target = getParamValue("target", reqParams);
					if (target != null) {
						params.put("target", target);
					} else {
						params.put("target", getTarget(getParamValue("targetId", reqParams)));
					}
					params.put("targetId", getParamValue("targetId", reqParams));

					postData(params);
				} catch (Exception e) {
					LOG.error("Error in UserActivity service for: " + paramsToString(reqParams), e);
				} finally {
					PERF_LOG.info(PerfMonitor.endRequest().toStringLog());
				}
			}
		});
	}

	public void addActivity(final UserActivity userActivity) {

		taskExecutor.submit(new Runnable() {
			@Override
			public void run() {
				if (Constant.getUserActivityServiceURL() == null || Constant.getUserActivityServiceURL().isEmpty()) {
					return;
				}
				PerfMonitor.startRequest("U:" + userActivity.getUserId(), "activityTrack");
				try {
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
				} catch (Exception e) {
					LOG.error("Error in UserActivity service for: " + userActivity, e);
				} finally {
					PERF_LOG.info(PerfMonitor.endRequest().toStringLog());
				}
			}
		});
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

	private final String getParamValue(String name, Map<String, String[]> params) {
		String[] arr = params.get(name);
		if (arr != null) {
			return arr[0];
		}
		return null;
	}

	private final String paramsToString(Map<String, String[]> params) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String[]> entry : params.entrySet()) {
			sb.append(entry.getKey()).append("->").append(Arrays.toString(entry.getValue())).append(NL);
		}
		return sb.toString();
	}

}
