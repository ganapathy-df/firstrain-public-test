package com.firstrain.frapi.domain;

import java.io.Serializable;
import java.util.Set;

public class IndustryBriefDomain implements Serializable {

	private static final long serialVersionUID = 8580197422484982267L;
	private int[] companyIdsArray;
	private int[] topicIdsArray;
	private Set<String> categoryIdsSet;
	private int topicId;
	private String topicToken;

	public Set<String> getCategoryIdsSet() {
		return categoryIdsSet;
	}

	public void setCategoryIdsSet(Set<String> categoryIdsSet) {
		this.categoryIdsSet = categoryIdsSet;
	}

	public int[] getTopicIdsArray() {
		return topicIdsArray;
	}

	public void setTopicIdsArray(int[] topicIdsArray) {
		this.topicIdsArray = topicIdsArray;
	}

	public int[] getCompanyIdsArray() {
		return companyIdsArray;
	}

	public void setCompanyIdsArray(int[] companyIdsArray) {
		this.companyIdsArray = companyIdsArray;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public String getTopicToken() {
		return topicToken;
	}

	public void setTopicToken(String topicToken) {
		this.topicToken = topicToken;
	}
}
