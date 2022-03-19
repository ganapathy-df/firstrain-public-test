package com.firstrain.web.exception;

public class PermissionException extends RuntimeException {

	public PermissionException() {
		super();
	}

	public PermissionException(String message) {
		super(message);
	}
}
