package com.firstrain.web.domain;

import java.util.List;
import java.util.Map;

public class Brand {

	private Map<String, PwToken> pwTokenMap;
	private Map<Integer, Group> groupMap;
	private Map<String, Topic> topicMap;
	private Map<String, List<String>> excludeTopicMap;
	private Map<String, List<String>> andTopicMap;
	private String brandInitials;

	public Map<String, PwToken> getPwTokenMap() {
		return pwTokenMap;
	}

	public void setPwTokenMap(Map<String, PwToken> pwTokenMap) {
		this.pwTokenMap = pwTokenMap;
	}

	public Map<Integer, Group> getGroupMap() {
		return groupMap;
	}

	public void setGroupMap(Map<Integer, Group> groupMap) {
		this.groupMap = groupMap;
	}

	public Map<String, Topic> getTopicMap() {
		return topicMap;
	}

	public void setTopicMap(Map<String, Topic> topicMap) {
		this.topicMap = topicMap;
	}

	public Map<String, List<String>> getExcludeTopicMap() {
		return excludeTopicMap;
	}

	public void setExcludeTopicMap(Map<String, List<String>> excludeTopicMap) {
		this.excludeTopicMap = excludeTopicMap;
	}

	public Map<String, List<String>> getAndTopicMap() {
		return andTopicMap;
	}

	public void setAndTopicMap(Map<String, List<String>> andTopicMap) {
		this.andTopicMap = andTopicMap;
	}

	public String getBrandInitials() {
		return brandInitials;
	}

	public void setBrandInitials(String brandInitials) {
		this.brandInitials = brandInitials;
	}

}
