package com.firstrain.web.wrapper;

import java.util.List;

import com.firstrain.frapi.domain.Entity;



public class EntityMap {
	private List<Entity> industry;
	private List<Entity> sector;
	private List<Entity> segment;
	private List<Entity> country;
	private List<Entity> businessLines;
	private List<String> language;
	private String domain;
	private String website;
	private String streetAddress;
	private String city;
	private String state;
	private String zip;
	private String companyLogo;

	public String getCompanyLogo() {
		return companyLogo;
	}

	public void setCompanyLogo(String companyLogo) {
		this.companyLogo = companyLogo;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public List<String> getLanguage() {
		return language;
	}

	public void setLanguage(List<String> language) {
		this.language = language;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public List<Entity> getIndustry() {
		return industry;
	}

	public void setIndustry(List<Entity> industry) {
		this.industry = industry;
	}

	public List<Entity> getSector() {
		return sector;
	}

	public void setSector(List<Entity> sector) {
		this.sector = sector;
	}

	public List<Entity> getSegment() {
		return segment;
	}

	public void setSegment(List<Entity> segment) {
		this.segment = segment;
	}

	public List<Entity> getCountry() {
		return country;
	}

	public void setCountry(List<Entity> country) {
		this.country = country;
	}

	public List<Entity> getBusinessLines() {
		return businessLines;
	}

	public void setBusinessLines(List<Entity> businessLines) {
		this.businessLines = businessLines;
	}
}
