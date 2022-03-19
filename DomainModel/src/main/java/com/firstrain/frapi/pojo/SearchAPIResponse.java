package com.firstrain.frapi.pojo;

import java.util.Map;

import com.firstrain.frapi.pojo.wrapper.DocumentSet;

public class SearchAPIResponse {

	private DocumentSet webResults;
	private int statusCode;
	private Map<String, Entity> tokenVsEntityMap;

	/**
	 * @return the webResults
	 */
	public DocumentSet getWebResults() {
		return webResults;
	}

	/**
	 * @param webResults the webResults to set
	 */
	public void setWebResults(DocumentSet webResults) {
		this.webResults = webResults;
	}


	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public Map<String, Entity> getTokenVsEntityMap() {
		return tokenVsEntityMap;
	}

	public void setTokenVsEntityMap(Map<String, Entity> tokenVsEntityMap) {
		this.tokenVsEntityMap = tokenVsEntityMap;
	}
}
