package com.common.exception;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    public ValidationException(String field, String message) {
        ErrorDetail errorDetail = new ErrorDetail(field, message);
        this.errors = List.of(errorDetail);
    }

    private List<ErrorDetail> errors;

}
