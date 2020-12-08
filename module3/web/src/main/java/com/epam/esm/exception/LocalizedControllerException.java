package com.epam.esm.exception;

import org.springframework.http.HttpStatus;

public class LocalizedControllerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int errorCode;
    private HttpStatus status;

    public int getErrorCode() {
        return errorCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public LocalizedControllerException(String message, int errorCode, HttpStatus status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public LocalizedControllerException(String message) {
        super(message);
    }

    public LocalizedControllerException() {
    }

    public LocalizedControllerException(Exception ex) {
        super(ex);
    }

    public LocalizedControllerException(String message, Exception ex) {
        super(message, ex);
    }
}
