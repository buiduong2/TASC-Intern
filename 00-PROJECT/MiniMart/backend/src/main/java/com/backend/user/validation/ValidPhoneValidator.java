package com.backend.user.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidPhoneValidator implements ConstraintValidator<ValidPhone, String> {

    private final Pattern pattern = Pattern.compile("^(?:\\+84|0)(3|5|7|8|9)\\d{8}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        Matcher matcher = pattern.matcher(value);

        return matcher.matches();
    }

}
