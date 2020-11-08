package com.epam.esm.exception;

import lombok.Getter;

public class LocalizedControllerException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    @Getter
    private ExceptionDetail exceptionDetail;

    public LocalizedControllerException(ExceptionDetail exceptionDetails) {
        super(exceptionDetails.getMessage());
        this.exceptionDetail = exceptionDetails;
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
