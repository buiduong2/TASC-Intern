package com.backend.common.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.backend.common.exception.ConflictException;
import com.backend.common.exception.GenericErrorResponse;
import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.exception.StockConflictResponse;
import com.backend.common.exception.ValidationErrorResponse;
import com.backend.common.exception.ValidationErrorResponse.ErrorDetail;
import com.backend.common.exception.ValidationException;
import com.backend.common.exception.StockConflictResponse.ConflictDetail;
import com.backend.order.exception.InvalidSignatureException;
import com.backend.order.exception.NotEnoughStockException;
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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<GenericErrorResponse> handleDataIntegrityViolation(ConstraintViolationException ex) {
        GenericErrorResponse error = GenericErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Duplicate Resource")
                .message("Duplicate or constraint violation")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<GenericErrorResponse> handleConflictException(ConflictException ex) {
        GenericErrorResponse error = GenericErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("Conflict data")
                .message("Cannot delete because it's already used")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(
            ValidationException ex) {
        ValidationErrorResponse errorResponse = new ValidationErrorResponse();
        ErrorDetail errorDetail = new ErrorDetail();
        errorDetail.setField(ex.getFiled());
        errorDetail.setMessage(ex.getMessage());
        errorResponse.setErrors(List.of(errorDetail));

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("JWT_INVALID")
                        .message("Invalid or expired token")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(InvalidSignatureException.class)
    public ResponseEntity<GenericErrorResponse> handleInvalidSignatureException(InvalidSignatureException ex) {
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(GenericErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("PAYMENT_SIGNATURE_INVALID")
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 500);
        body.put("error", "Internal Server Error");
        body.put("message", "Error server");
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body);
    }

    @ExceptionHandler(NotEnoughStockException.class)
    public ResponseEntity<StockConflictResponse> handleNotEnoughStockException(NotEnoughStockException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                StockConflictResponse.builder()
                        .code("INSUFFICIENT_STOCK")
                        .message(ex.getMessage())
                        .status(HttpStatus.CONFLICT.value())
                        .timestamp(LocalDateTime.now())
                        .detail(ConflictDetail.builder()
                                .productId(ex.getProductId())
                                .availableQuantity(ex.getAvailableQuantity())
                                .requestedQuantity(ex.getRequestedQuantity())
                                .build())
                        .build()

        );
    }
}
