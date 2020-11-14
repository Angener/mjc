package com.epam.esm.controller;

import com.epam.esm.exception.LocalizedControllerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionControllerAdviser {
    private final ResourceBundleMessageSource messageSource;

    @Autowired
    public GlobalExceptionControllerAdviser(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({LocalizedControllerException.class})
    public ResponseEntity<Object> handleException(LocalizedControllerException ex, Locale locale) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("errorMessage", resolveResourceBundle(ex, locale));
        parameters.put("errorCode", ex.getErrorCode());
        return new ResponseEntity<>(parameters, ex.getStatus());
    }

    private String resolveResourceBundle(Exception ex, Locale locale) {
        return messageSource.getMessage(ex.getMessage(), null, locale);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleUnregisterException(Locale locale) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("errorMessage", resolveResourceBundle(locale));
        parameters.put("errorCode", 50001);
        return new ResponseEntity<>(parameters, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String resolveResourceBundle(Locale locale) {
        return messageSource.getMessage("exception.message.50001", null, locale);
    }
}
