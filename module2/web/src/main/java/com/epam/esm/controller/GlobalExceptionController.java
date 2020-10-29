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
public class GlobalExceptionController {
    @Autowired
    private ResourceBundleMessageSource messageSource;

    @ExceptionHandler({LocalizedControllerException.class})
    public ResponseEntity<Object> handleException(LocalizedControllerException ex, Locale locale) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("errorMessage", resolveResourceBundle(ex, locale));
        parameters.put("errorCode", ex.getExceptionDetail().getErrorCode());
        return new ResponseEntity<>(parameters, HttpStatus.NOT_FOUND);
    }

    private String resolveResourceBundle(Exception ex, Locale locale) {
        return messageSource.getMessage(ex.getMessage(), new Object[]{null}, locale);
    }
}