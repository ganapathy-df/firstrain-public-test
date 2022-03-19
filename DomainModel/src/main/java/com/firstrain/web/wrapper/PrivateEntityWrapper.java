package com.firstrain.web.wrapper;

import java.util.List;

import org.codehaus.jackson.JsonNode;

import com.firstrain.web.pojo.ResultJsonRes;
import com.firstrain.web.pojo.Version;

public class PrivateEntityWrapper {

	private String name;
	private String searchToken;
	private String jobId;
	private String state;
	private String createdDate;
	private String lastModifiedBy;
	private String lastModifiedDate;
	private JsonNode definition;
	private String progress;
	private List<ResultJsonRes> documents;
	private List<PrivateEntityWrapper> entities;
	private List<Version> versions;

	public List<PrivateEntityWrapper> getEntities() {
		return entities;
	}

	public void setEntities(List<PrivateEntityWrapper> entities) {
		this.entities = entities;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSearchToken() {
		return searchToken;
	}

	public void setSearchToken(String searchToken) {
		this.searchToken = searchToken;
	}

	public String getJobId() {
		return jobId;
	}

	public void setJobId(String jobId) {
		this.jobId = jobId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public JsonNode getDefinition() {
		return definition;
	}

	public void setDefinition(JsonNode definition) {
		this.definition = definition;
	}

	public String getProgress() {
		return progress;
	}

	public void setProgress(String progress) {
		this.progress = progress;
	}

	public List<ResultJsonRes> getDocuments() {
		return documents;
	}

	public void setDocuments(List<ResultJsonRes> documents) {
		this.documents = documents;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(String lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public List<Version> getVersions() {
		return versions;
	}

	public void setVersions(List<Version> versions) {
		this.versions = versions;
	}
}
