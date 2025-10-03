package com.common.utils;

import java.time.LocalDateTime;

import com.common.exception.GenericErrorResponse;
import com.common.exception.GenericException;

public class ErrorResponseFactory {
    public static GenericErrorResponse from(GenericException ex) {
        return GenericErrorResponse.builder()
                .status(ex.getStatus())
                .error(ex.getError())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
