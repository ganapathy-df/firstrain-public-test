package com.firstrain.frapi.domain;

import java.util.List;



public class EntityMap {
	private Entity entity;
	private Entity industry;
	private Entity sector;
	private Entity segment;
	private List<Entity> bizlines;
	private List<String> language;
	private Entity country;
	private String domain;
	private String companyLogo;

	public Entity getIndustry() {
		return this.industry;
	}

	public void setIndustry(Entity industry) {
		this.industry = industry;
	}

	public Entity getSector() {
		return this.sector;
	}

	public void setSector(Entity sector) {
		this.sector = sector;
	}

	public Entity getSegment() {
		return this.segment;
	}

	public void setSegment(Entity segment) {
		this.segment = segment;
	}

	public List<Entity> getBizlines() {
		return this.bizlines;
	}

	public void setBizlines(List<Entity> bizlines) {
		this.bizlines = bizlines;
	}

	public Entity getEntity() {
		return this.entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public List<String> getLanguage() {
		return language;
	}

	public void setLanguage(List<String> language) {
		this.language = language;
	}

	public Entity getCountry() {
		return country;
	}

	public void setCountry(Entity country) {
		this.country = country;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}
}
