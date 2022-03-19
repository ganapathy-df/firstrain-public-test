package com.firstrain.web.exception;

public class CustomExceptionSuccess extends RuntimeException {

	private int errorCode;

	public CustomExceptionSuccess() {
		super();
	}

	public CustomExceptionSuccess(String message) {
		super(message);
	}

	public CustomExceptionSuccess(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public CustomExceptionSuccess(String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
