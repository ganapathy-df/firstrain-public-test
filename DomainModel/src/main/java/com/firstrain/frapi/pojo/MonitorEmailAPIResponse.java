package com.firstrain.frapi.pojo;

import java.util.List;

import com.firstrain.frapi.domain.MonitorEmail;

public class MonitorEmailAPIResponse {

	private int statusCode;
	private String monitorName;
	private long monitorId;
	private String emailTemplate;
	private List<MonitorEmail> emails;

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
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

	public List<MonitorEmail> getEmails() {
		return emails;
	}

	public void setEmails(List<MonitorEmail> emails) {
		this.emails = emails;
	}
}
