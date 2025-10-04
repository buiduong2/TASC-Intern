package com.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ ElementType.PARAMETER, ElementType.TYPE_USE })
@Constraint(validatedBy = ImageValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface Image {

    String[] allowedType() default {};

    int maxSize() default 2097152;

    String message() default "Invalid Image File";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
