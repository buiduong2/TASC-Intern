package com.authentication_service.exception;

import java.util.List;

import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthValidationException extends AuthenticationException {
    private List<FieldError> errors;

    public AuthValidationException(String message, List<FieldError> errors) {
        super(message);
        this.errors = errors;
    }

}
