package com.authentication_service.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericException extends RuntimeException {
    private int status;
    private String error;
    private String message;

    public GenericException(ErrorCode errorCode) {
        this.status = errorCode.getStatus().value();
        this.error = errorCode.getStatus().getReasonPhrase();
        this.message = errorCode.getTemplate();
    }
}
