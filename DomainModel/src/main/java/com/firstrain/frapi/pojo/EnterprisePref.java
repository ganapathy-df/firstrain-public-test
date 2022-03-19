package com.firstrain.frapi.pojo;

import java.util.Map;

import com.firstrain.frapi.domain.SectionSpec;
import com.firstrain.frapi.pojo.wrapper.BaseSet.SectionType;

public class EnterprisePref {

	private long enterpriseId;
	private Short industryClassificationId;
	private String privateSourceIdsSSV;
	private String publicSourceIdsSSV;
	private Map<SectionType, SectionSpec> sectionsMap;
	private int searchesPerMonitor;
	private String customizedCssFileName;
	private boolean isDnBId;
	private boolean applyMinNodeCheckInVisualization = true;

	public long getEnterpriseId() {
		return enterpriseId;
	}

	public void setEnterpriseId(long enterpriseId) {
		this.enterpriseId = enterpriseId;
	}

	public Short getIndustryClassificationId() {
		return industryClassificationId;
	}

	public void setIndustryClassificationId(Short industryClassificationId) {
		this.industryClassificationId = industryClassificationId;
	}

	public String getPrivateSourceIdsSSV() {
		return privateSourceIdsSSV;
	}

	public void setPrivateSourceIdsSSV(String privateSourceIdsSSV) {
		this.privateSourceIdsSSV = privateSourceIdsSSV;
	}

	public String getPublicSourceIdsSSV() {
		return publicSourceIdsSSV;
	}

	public void setPublicSourceIdsSSV(String publicSourceIdsSSV) {
		this.publicSourceIdsSSV = publicSourceIdsSSV;
	}

	public Map<SectionType, SectionSpec> getSectionsMap() {
		return sectionsMap;
	}

	public void setSectionsMap(Map<SectionType, SectionSpec> sectionsMap) {
		this.sectionsMap = sectionsMap;
	}

	public int getSearchesPerMonitor() {
		return searchesPerMonitor;
	}

	public void setSearchesPerMonitor(int searchesPerMonitor) {
		this.searchesPerMonitor = searchesPerMonitor;
	}

	public String getCustomizedCssFileName() {
		return customizedCssFileName;
	}

	public void setCustomizedCssFileName(String customizedCssFileName) {
		this.customizedCssFileName = customizedCssFileName;
	}

	public boolean isDnBId() {
		return isDnBId;
	}

	public void setDnBId(boolean isDnBId) {
		this.isDnBId = isDnBId;
	}

	public boolean isApplyMinNodeCheckInVisualization() {
		return applyMinNodeCheckInVisualization;
	}

	public void setApplyMinNodeCheckInVisualization(boolean applyMinNodeCheckInVisualization) {
		this.applyMinNodeCheckInVisualization = applyMinNodeCheckInVisualization;
	}
}
