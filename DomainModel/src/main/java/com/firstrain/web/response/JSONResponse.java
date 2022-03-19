package com.firstrain.web.response;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "response")
public class JSONResponse<T> {

	private ResStatus status;
	private String message;
	private String errorStackTrace;
	private Integer errorCode;
	private String version;
	private T result;

	public JSONResponse() {}

	public ResStatus getStatus() {
		return status;
	}

	public void setStatus(ResStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String msg) {
		this.message = msg;
	}

	public String getErrorStackTrace() {
		return errorStackTrace;
	}

	public void setErrorStackTrace(String errorStackTrace) {
		this.errorStackTrace = errorStackTrace;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T data) {
		this.result = data;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return String.format("JSONResponse [status=%s, message=%s, errorStackTrace=%s, errorCode=%s, version=%s, result=%s]", this.status,
				this.message, this.errorStackTrace, this.errorCode, this.version, this.result);
	}

	public enum ResStatus {
		SUCCESS, PARTIAL_SUCCESS, WARNING, ERROR;

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}
}
