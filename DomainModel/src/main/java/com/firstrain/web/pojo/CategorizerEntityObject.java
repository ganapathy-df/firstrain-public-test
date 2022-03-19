package com.firstrain.web.pojo;

public class CategorizerEntityObject {

	private int responseCode;
	private CatEntityObjectWrapper data;

	public CategorizerEntityObject() {}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public CatEntityObjectWrapper getData() {
		return data;
	}

	public void setData(CatEntityObjectWrapper data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "CategorizerEntityObject [responseCode=" + responseCode + ", data=" + data + "]";
	}

	public static class CatEntityObjectWrapper {
		private boolean valid;
		private String detailedMessage;

		public boolean isValid() {
			return valid;
		}

		public void setValid(boolean valid) {
			this.valid = valid;
		}

		public String getDetailedMessage() {
			return detailedMessage;
		}

		public void setDetailedMessage(String detailedMessage) {
			this.detailedMessage = detailedMessage;
		}

	}
}
