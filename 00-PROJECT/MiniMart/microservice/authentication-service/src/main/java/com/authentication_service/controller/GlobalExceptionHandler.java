package com.authentication_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.common.exception.GenericException;
import com.common.exception.ValidationErrorResponse;
import com.common.utils.ErrorResponseFactory;
import com.common.utils.ValidationErrorResponseFactory;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(
            ConstraintViolationException cve) {
        return new ResponseEntity<>(ValidationErrorResponseFactory.from(cve), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ValidationErrorResponseFactory.from(ex));
    }

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> handleGenericException(GenericException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponseFactory.from(ex));
    }
}
