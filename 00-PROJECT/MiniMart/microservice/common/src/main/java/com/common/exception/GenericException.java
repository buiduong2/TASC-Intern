package com.common.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenericException extends RuntimeException {
    private int status;
    private String error;
    private String message;

    public GenericException(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.error = errorCode.getError();
        this.message = errorCode.getTemplate();
    }

    public GenericException(ErrorCode errorCode, Object... messageArg) {
        this.status =  errorCode.getStatus();
        this.error =  errorCode.getError();
        this.message = errorCode.getTemplateFormat(messageArg);
    }
}