package com.firstrain.frapi.domain;

import java.util.List;

public class MonitorConfig {

	private String monitorId;
	private String monitorName;
	private Long ownedBy;
	private String ownedByType;
	private List<String> filters;
	private List<ItemDetail> queries;

	public List<String> getFilters() {
		return filters;
	}

	public void setFilters(List<String> filters) {
		this.filters = filters;
	}

	public List<ItemDetail> getQueries() {
		return queries;
	}

	public void setQueries(List<ItemDetail> queries) {
		this.queries = queries;
	}

	public String getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(String monitorId) {
		this.monitorId = monitorId;
	}

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public Long getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(Long ownedBy) {
		this.ownedBy = ownedBy;
	}

	public String getOwnedByType() {
		return ownedByType;
	}

	public void setOwnedByType(String ownedByType) {
		this.ownedByType = ownedByType;
	}

}
