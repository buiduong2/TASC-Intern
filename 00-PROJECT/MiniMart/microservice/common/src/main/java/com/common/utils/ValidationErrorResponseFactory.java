package com.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.MethodArgumentNotValidException;

import com.common.exception.ValidationErrorResponse;
import com.common.exception.ValidationErrorResponse.ErrorDetail;

import jakarta.validation.ConstraintViolationException;

public final class ValidationErrorResponseFactory {

    private ValidationErrorResponseFactory() {

    }

    public static ValidationErrorResponse from(ConstraintViolationException cve) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.setErrors(errorDetails);

        cve.getConstraintViolations().forEach(cv -> {
            var errorDetail = new ErrorDetail();
            String path = cv.getPropertyPath().toString();
            String field = path.substring(path.indexOf('.') + 1);

            errorDetail.setField(field);
            errorDetail.setMessage(cv.getMessage());

            errorDetails.add(errorDetail);
        });
        return errorResponse;
    }

    public static ValidationErrorResponse from(MethodArgumentNotValidException ex) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.setErrors(errorDetails);

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            var errorDetail = new ErrorDetail();
            errorDetail.setField(error.getField());
            errorDetail.setMessage(error.getDefaultMessage());

            errorDetails.add(errorDetail);
        });
        return errorResponse;
    }
}
