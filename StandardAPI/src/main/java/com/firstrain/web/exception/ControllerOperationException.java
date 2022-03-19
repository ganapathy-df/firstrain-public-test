package com.firstrain.web.exception;

public class ControllerOperationException extends RuntimeException {

    private static final long serialVersionUID = -5192967485093518942L;

    public ControllerOperationException() {
        super();
    }

    public ControllerOperationException(String message) {
        super(message);
    }
}
