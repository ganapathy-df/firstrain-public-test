package com.firstrain.web.domain;

import java.util.Map;

import com.firstrain.web.pojo.CategorizerObject.CatEntity;

public class MultipleMatchedEntities {

	private Map<String, CatEntity> matchedTaxonomyMap;
	private Map<String, CatEntity> companyMap;
	private Map<String, CatEntity> peCompanyMap;
	private Map<String, CatEntity> peTopicMap;

	public Map<String, CatEntity> getMatchedTaxonomyMap() {
		return matchedTaxonomyMap;
	}

	public void setMatchedTaxonomyMap(Map<String, CatEntity> matchedTaxonomyMap) {
		this.matchedTaxonomyMap = matchedTaxonomyMap;
	}

	public Map<String, CatEntity> getCompanyMap() {
		return companyMap;
	}

	public void setCompanyMap(Map<String, CatEntity> companyMap) {
		this.companyMap = companyMap;
	}

	public Map<String, CatEntity> getPeCompanyMap() {
		return peCompanyMap;
	}

	public void setPeCompanyMap(Map<String, CatEntity> peCompanyMap) {
		this.peCompanyMap = peCompanyMap;
	}

	public Map<String, CatEntity> getPeTopicMap() {
		return peTopicMap;
	}

	public void setPeTopicMap(Map<String, CatEntity> peTopicMap) {
		this.peTopicMap = peTopicMap;
	}
}
