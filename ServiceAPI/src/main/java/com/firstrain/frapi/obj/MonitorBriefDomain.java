package com.firstrain.frapi.obj;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.firstrain.frapi.pojo.Items;


/**
 * MonitorBriefDomain: Contains The Objects That Are Filled Before Threads Start To Execute in The MonitorBriefProcessor
 * 
 * @author msingh
 *
 */
public class MonitorBriefDomain {

	private List<com.firstrain.frapi.pojo.Items> itemList;
	private Set<String> companyCatIds = new HashSet<String>();
	private Set<String> topicCatIds = new HashSet<String>();
	private int[] companyIdsArr;
	private String[] qList;
	private int[] scopeList;
	private Set<String> monitorEntities;
	private String bizlineCatIdCSV;

	private int industryCatId = -1;
	private boolean industryOnly;

	public int getIndustryCatId() {
		return industryCatId;
	}

	public void setIndustryCatId(int industryCatId) {
		this.industryCatId = industryCatId;
	}

	public boolean isIndustryOnly() {
		return industryOnly;
	}

	public void setIndustryOnly(boolean industryOnly) {
		this.industryOnly = industryOnly;
	}

	public String[] getqList() {
		return qList;
	}

	public void setqList(String[] qList) {
		this.qList = qList;
	}

	public int[] getScopeList() {
		return scopeList;
	}

	public void setScopeList(int[] scopeList) {
		this.scopeList = scopeList;
	}

	public List<Items> getItemList() {
		return itemList;
	}

	public void setItemList(List<Items> itemList) {
		this.itemList = itemList;
	}

	public Set<String> getCompanyCatIds() {
		return companyCatIds;
	}

	public void setCompanyCatIds(Set<String> companyCatIds) {
		this.companyCatIds = companyCatIds;
	}

	public Set<String> getTopicCatIds() {
		return topicCatIds;
	}

	public void setTopicCatIds(Set<String> topicCatIds) {
		this.topicCatIds = topicCatIds;
	}

	public int[] getCompanyIdsArr() {
		return companyIdsArr;
	}

	public void setCompanyIdsArr(int[] companyIdsArr) {
		this.companyIdsArr = companyIdsArr;
	}

	public Set<String> getMonitorEntities() {
		return monitorEntities;
	}

	public void setMonitorEntities(Set<String> monitorEntities) {
		this.monitorEntities = monitorEntities;
	}

	public String getBizlineCatIdCSV() {
		return bizlineCatIdCSV;
	}

	public void setBizlineCatIdCSV(String bizlineCatIdCSV) {
		this.bizlineCatIdCSV = bizlineCatIdCSV;
	}
}
