package com.epam.esm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LocalizedControllerException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int errorCode;
    private HttpStatus status;

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
