package com.firstrain.frapi.pojo;

public class EmailResponse {

	private int statusCode;
	private String emailName;
	private long emailId;
	private String emailTemplate;
	private EmailDetail emailDetail;

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

	public String getEmailName() {
		return emailName;
	}

	public void setEmailName(String emailName) {
		this.emailName = emailName;
	}

	public long getEmailId() {
		return emailId;
	}

	public void setEmailId(long emailId) {
		this.emailId = emailId;
	}

	public EmailDetail getEmailDetail() {
		return emailDetail;
	}

	public void setEmailDetail(EmailDetail emailDetail) {
		this.emailDetail = emailDetail;
	}

}
