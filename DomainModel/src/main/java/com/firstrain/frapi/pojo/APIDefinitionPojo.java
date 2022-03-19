package com.firstrain.frapi.pojo;


public class APIDefinitionPojo {

	private long id;
	private String apiVersion;
	private String apiSignature;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getApiVersion() {
		return apiVersion;
	}

	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

	public String getApiSignature() {
		return apiSignature;
	}

	public void setApiSignature(String apiSignature) {
		this.apiSignature = apiSignature;
	}
}
