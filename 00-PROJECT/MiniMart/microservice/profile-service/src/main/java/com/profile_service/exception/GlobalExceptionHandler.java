package com.profile_service.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.profile_service.exception.ValidationErrorResponse.ErrorDetail;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GenericException.class)
    public ResponseEntity<?> handleGenericException(GenericException ex) {
        GenericErrorResponse error = GenericErrorResponse
                .builder()
                .status(ex.getStatus())
                .error(ex.getError())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ValidationErrorResponse> handleConstraintViolationException(
            ConstraintViolationException cve) {
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

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
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
