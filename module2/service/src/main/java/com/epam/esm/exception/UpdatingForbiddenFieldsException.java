package com.epam.esm.exception;

public class UpdatingForbiddenFieldsException extends Exception {
    private static final long serialVersionUID = 1L;

    public UpdatingForbiddenFieldsException() {
    }

    public UpdatingForbiddenFieldsException(String message) {
        super(message);
    }

    public UpdatingForbiddenFieldsException(Exception ex) {
        super(ex);
    }

    public UpdatingForbiddenFieldsException(String message, Exception ex) {
        super(message, ex);
    }
}
