package com.backend.common.validation;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<Image, MultipartFile> {

    private List<String> allowedTypes;
    private int maxSize;
    private Pattern extensionPattern = Pattern
            .compile(".*\\.(jpg|jpeg|png|gif|avif)$");

    @Override
    public void initialize(Image constraintAnnotation) {
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowedType());
        this.maxSize = constraintAnnotation.maxSize();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value.isEmpty()) {
            return false;
        }

        context.disableDefaultConstraintViolation();
        if (this.maxSize < value.getSize()) {
            context.buildConstraintViolationWithTemplate("File size exceeds the maximum size: " + maxSize)
                    .addConstraintViolation();
            return false;
        }

        String contentType = value.getContentType();
        if (contentType == null) {
            context.buildConstraintViolationWithTemplate("Content Type must be not null: ")
                    .addConstraintViolation();
            return false;
        }

        if (this.allowedTypes.size() == 0) {
            if (!contentType.startsWith("image/")) {
                context.buildConstraintViolationWithTemplate("Content Type must be an Image ")
                        .addConstraintViolation();
                return false;
            }
        } else if (!this.allowedTypes.contains(contentType)) {
            context.buildConstraintViolationWithTemplate("Content Type must matches:  " + this.allowedTypes)
                    .addConstraintViolation();
            return false;
        }

        String fileName = value.getOriginalFilename();
        if (fileName == null || !this.extensionPattern.matcher(fileName).matches()) {
            context.buildConstraintViolationWithTemplate("Extension of file isInvalid:  ")
                    .addConstraintViolation();
            return false;
        }

        return true;
    }

}
