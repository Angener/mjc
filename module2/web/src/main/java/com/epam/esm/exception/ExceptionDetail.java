package com.epam.esm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ExceptionDetail {

    TAG_NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "exception.message.40401", 40401),
    TAG_DOES_NOT_CONTAIN_NAME(HttpStatus.BAD_REQUEST, "exception.message.40001", 40001),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "exception.message.50001", 50001),
    UPDATING_FORBIDDEN_DATE_FIELDS(HttpStatus.FORBIDDEN, "exception.message.40301",40301);

    @Getter private final HttpStatus httpStatus;
    @Getter private final String message;
    @Getter private final int errorCode;
}
