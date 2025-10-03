package com.common.exception;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationErrorResponse {
    private int status = 400;
    private String error = "Validation Error";
    private String message = "Input validation Failed";
    private LocalDateTime timestamp = LocalDateTime.now();
    private List<ErrorDetail> errors;

    @Getter
    @Setter
    public static class ErrorDetail {
        private String field;
        private String message;

    }
}
