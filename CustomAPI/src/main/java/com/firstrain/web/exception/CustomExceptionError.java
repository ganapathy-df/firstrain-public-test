package com.firstrain.web.exception;

@SuppressWarnings("serial")
public class CustomExceptionError extends RuntimeException {

	private int errorCode;
	private boolean isCustomMessage;

	public CustomExceptionError() {
		super();
	}

	public CustomExceptionError(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public CustomExceptionError(String message) {
		super(message);
	}

	public CustomExceptionError(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public CustomExceptionError(String message, int errorCode, boolean isCustoMessage) {
		super(message);
		this.errorCode = errorCode;
		this.isCustomMessage = isCustoMessage;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public boolean isCustomMessage() {
		return isCustomMessage;
	}

}
