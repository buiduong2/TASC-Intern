package com.backend.common.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.common.exception.GenericErrorResponse;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.exception.ValidationErrorResponse;
import com.backend.common.exception.ValidationErrorResponse.ErrorDetail;
import com.backend.user.exception.TokenBlacklistedException;
import com.backend.user.exception.TokenVersionMismatchException;
import com.backend.user.exception.UserInactiveException;

import io.jsonwebtoken.JwtException;

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

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<GenericErrorResponse> handleUserInactiveException(UserInactiveException ex) {
        GenericErrorResponse error = GenericErrorResponse
                .builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("USER_INACTIVE")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GenericErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        GenericErrorResponse error = GenericErrorResponse
                .builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("AUTH_INVALID")
                .message("Invalid username or password")
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(TokenVersionMismatchException.class)
    public ResponseEntity<GenericErrorResponse> handleTokenVersionMismatch(TokenVersionMismatchException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("TOKEN_VERSION_MISMATCH")
                        .message("Token is no longer valid, please re-login")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(TokenBlacklistedException.class)
    public ResponseEntity<GenericErrorResponse> handleTokenBlacklisted(TokenBlacklistedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("TOKEN_BLACKLISTED")
                        .message("Token has been revoked")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<GenericErrorResponse> handleJwtException(JwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("JWT_INVALID")
                        .message("Invalid or expired token")
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}
