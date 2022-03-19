package com.firstrain.frapi.pojo;

import java.util.List;

import com.firstrain.frapi.domain.EntityStatus;
import com.firstrain.frapi.domain.MonitorConfig;
import com.firstrain.frapi.domain.MonitorDetails;
import com.firstrain.frapi.domain.MonitorInfo;

public class MonitorAPIResponse {

	private int statusCode;
	private String monitorName;
	private long monitorId;
	private MonitorInfo monitorInfo;
	private EntityStatus entityStatus;
	private MonitorBriefDetail monitorBriefDetail;

	public MonitorBriefDetail getMonitorBriefDetail() {
		return monitorBriefDetail;
	}

	public void setMonitorBriefDetail(MonitorBriefDetail monitorBriefDetail) {
		this.monitorBriefDetail = monitorBriefDetail;
	}

	public EntityStatus getEntityStatus() {
		return entityStatus;
	}

	public void setEntityStatus(EntityStatus entityStatus) {
		this.entityStatus = entityStatus;
	}

	private MonitorDetails monitorDetails;
	private List<Entity> entities;
	private MonitorConfig monitorConfig;

	public MonitorConfig getMonitorConfig() {
		return monitorConfig;
	}

	public void setMonitorConfig(MonitorConfig monitorConfig) {
		this.monitorConfig = monitorConfig;
	}

	public MonitorDetails getMonitorDetails() {
		return monitorDetails;
	}

	public void setMonitorDetails(MonitorDetails monitorDetails) {
		this.monitorDetails = monitorDetails;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMonitorName() {
		return monitorName;
	}

	public void setMonitorName(String monitorName) {
		this.monitorName = monitorName;
	}

	public long getMonitorId() {
		return monitorId;
	}

	public void setMonitorId(long monitorId) {
		this.monitorId = monitorId;
	}

	public MonitorInfo getMonitorInfo() {
		return monitorInfo;
	}

	public void setMonitorInfo(MonitorInfo monitorInfo) {
		this.monitorInfo = monitorInfo;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void setEntities(List<Entity> entities) {
		this.entities = entities;
	}
}
