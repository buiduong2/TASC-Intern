package com.common.validation;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValueValidator implements ConstraintValidator<EnumValue, String> {

    private List<String> enumConstants;
    private String message;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        this.enumConstants = Arrays.stream(constraintAnnotation.value().getEnumConstants())
                .map(e -> e.toString())
                .toList();

        this.message = "String must be matches " + String.join("|", this.enumConstants);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (this.enumConstants.contains(value)) {
            return true;
        }

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(this.message).addConstraintViolation();

        return false;
    }

}