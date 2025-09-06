package com.backend.common.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.common.dto.res.GenericErrorResponse;
import com.backend.common.dto.res.ValidationErrorResponse;
import com.backend.common.dto.res.ValidationErrorResponse.ErrorDetail;
import com.backend.common.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericErrorResponse> handleREsourseNotFoundException(ResourceNotFoundException ex) {
        GenericErrorResponse error = GenericErrorResponse
                .builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Resource Not Found")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        errorResponse.setErrors(errorDetails);
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            var errorDetail = new ErrorDetail();
            errorDetail.setField(error.getField());
            errorDetail.setMessage(error.getDefaultMessage());

            errorDetails.add(errorDetail);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
