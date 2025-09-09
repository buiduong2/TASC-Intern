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
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        switch (this.column) {
            case EMAIL:
                return !repository.existsByEmail(value);
            case USERNAME:
                return !repository.existsByUsername(value);
            default:
                throw new IllegalArgumentException("Column type not implemented Yet");
        }

    }

}
