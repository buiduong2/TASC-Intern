package com.backend.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationException extends RuntimeException {

    private String filed;
    private String message;
}
