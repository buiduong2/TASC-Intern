package com.authentication_service.validation;

import org.springframework.stereotype.Service;

import com.authentication_service.repository.SystemUserRepository;
import com.authentication_service.validation.UserUniqueField.Column;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserUniqueFieldValidator implements ConstraintValidator<UserUniqueField, String> {

    private final SystemUserRepository repository;
    private Column column;

    @Override
    public void initialize(UserUniqueField constraintAnnotation) {
        this.column = constraintAnnotation.column();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }
        return switch (this.column) {
            case EMAIL -> !repository.existsByEmail(value);

            case USERNAME -> !repository.existsByUsername(value);
        };

    }

}