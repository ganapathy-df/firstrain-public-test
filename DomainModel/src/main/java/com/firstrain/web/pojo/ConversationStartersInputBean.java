package com.firstrain.web.pojo;

import java.util.List;
import java.util.Map;

public class ConversationStartersInputBean {
	private Map<String, String> lead;
	private Map<String, String> leadCompany;
	private Map<String, String> tpUserInfo;
	private Map<String, List<String>> targetProductAreas;
	private Map<String, List<String>> industry;

	public Map<String, List<String>> getTargetProductAreas() {
		return targetProductAreas;
	}

	public void setTargetProductAreas(Map<String, List<String>> targetProductAreas) {
		this.targetProductAreas = targetProductAreas;
	}

	public Map<String, String> getLead() {
		return lead;
	}

	public void setLead(Map<String, String> lead) {
		this.lead = lead;
	}

	public Map<String, List<String>> getIndustry() {
		return industry;
	}

	public void setIndustry(Map<String, List<String>> industry) {
		this.industry = industry;
	}

	public Map<String, String> getLeadCompany() {
		return leadCompany;
	}

	public void setLeadCompany(Map<String, String> leadCompany) {
		this.leadCompany = leadCompany;
	}

	public Map<String, String> getTpUserInfo() {
		return tpUserInfo;
	}

	public void setTpUserInfo(Map<String, String> tpUserInfo) {
		this.tpUserInfo = tpUserInfo;
	}
}
