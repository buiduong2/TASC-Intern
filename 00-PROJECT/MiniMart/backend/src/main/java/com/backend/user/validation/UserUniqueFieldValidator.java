package com.backend.user.validation;

import org.springframework.stereotype.Service;

import com.backend.user.repository.UserRepository;
import com.backend.user.validation.UserUniqueField.Column;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserUniqueFieldValidator implements ConstraintValidator<UserUniqueField, String> {

    private final UserRepository repository;
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
