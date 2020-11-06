package com.epam.esm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum ExceptionDetail {


    TAG_DOES_NOT_CONTAIN_NAME(HttpStatus.BAD_REQUEST, "exception.message.40001", 40001),
    NAME_IS_NOT_UNIQUE(HttpStatus.BAD_REQUEST, "exception.message.40002", 40002),
    CERTIFICATE_TAGS_IS_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "exception.message.40003", 40003),
    UPDATING_FORBIDDEN_DATE_FIELDS(HttpStatus.FORBIDDEN, "exception.message.40301",40301),
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "exception.message.40401", 40401),
    GIFT_CERTIFICATE_NOT_FOUND(HttpStatus.NOT_FOUND, "exception.message.40402", 40402),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "exception.message.50001", 50001);


    @Getter private final HttpStatus httpStatus;
    @Getter private final String message;
    @Getter private final int errorCode;
}
