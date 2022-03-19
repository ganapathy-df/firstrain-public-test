package com.firstrain.frapi.pojo;

import java.sql.Timestamp;
import java.util.List;


public class AuthAPIResponse {

	private int statusCode;
	private String authKey;
	private Timestamp expiryTime;
	private long enterpriseId;
	private String apiVersion;
	private List<String> excludedSectionList;
	private List<String> excludedAPIList;
	private List<String> includedAPIList;
	private Timestamp versionStartDate;
	private Timestamp versionEndDate;
	private int apiThreshold;
	private String prefJson;
	private long id;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getAuthKey() {
		return authKey;
	}

	public void setAuthKey(String authKey) {
		this.authKey = authKey;
	}

	public Timestamp getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Timestamp expiryTime) {
		this.expiryTime = expiryTime;
	}

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public List<String> getExcludedSectionList() {
		return excludedSectionList;
	}

	public void setExcludedSectionList(List<String> excludedSectionList) {
		this.excludedSectionList = excludedSectionList;
	}

	public List<String> getExcludedAPIList() {
		return excludedAPIList;
	}

	public void setExcludedAPIList(List<String> excludedAPIList) {
		this.excludedAPIList = excludedAPIList;
	}

	public List<String> getIncludedAPIList() {
		return includedAPIList;
	}

	public void setIncludedAPIList(List<String> includedAPIList) {
		this.includedAPIList = includedAPIList;
	}

	public Timestamp getVersionStartDate() {
		return versionStartDate;
	}

	public void setVersionStartDate(Timestamp versionStartDate) {
		this.versionStartDate = versionStartDate;
	}

	public Timestamp getVersionEndDate() {
		return versionEndDate;
	}

	public void setVersionEndDate(Timestamp versionEndDate) {
		this.versionEndDate = versionEndDate;
	}

	public int getApiThreshold() {
		return apiThreshold;
	}

	public void setApiThreshold(int apiThreshold) {
		this.apiThreshold = apiThreshold;
	}

	public String getPrefJson() {
		return prefJson;
	}

	public void setPrefJson(String prefJson) {
		this.prefJson = prefJson;
	}
}
