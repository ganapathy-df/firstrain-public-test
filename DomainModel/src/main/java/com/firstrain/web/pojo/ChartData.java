package com.firstrain.web.pojo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "data")
public class ChartData {
	private String name;
	private String query;
	private String value;
	private String intensity;
	private String smartText;
	private String searchToken;
	private String countryCode;
	private String stateCode;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getIntensity() {
		return intensity;
	}

	public void setIntensity(String intensity) {
		this.intensity = intensity;
	}

	public String getSmartText() {
		return smartText;
	}

	public void setSmartText(String smartText) {
		this.smartText = smartText;
	}

	public String getSearchToken() {
		return searchToken;
	}

	public void setSearchToken(String searchToken) {
		this.searchToken = searchToken;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}
}
