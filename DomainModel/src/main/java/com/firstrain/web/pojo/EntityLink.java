package com.firstrain.web.pojo;

public class EntityLink {

	private String id;
	private String primaryTopic;
	private String primaryCompany;
	private String entityLinkingScore;

	public String getPrimaryCompany() {
		return primaryCompany;
	}

	public void setPrimaryCompany(String primaryCompany) {
		this.primaryCompany = primaryCompany;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPrimaryTopic() {
		return primaryTopic;
	}

	public void setPrimaryTopic(String primaryTopic) {
		this.primaryTopic = primaryTopic;
	}

	public String getEntityLinkingScore() {
		return entityLinkingScore;
	}

	public void setEntityLinkingScore(String entityLinkingScore) {
		this.entityLinkingScore = entityLinkingScore;
	}
}
