package com.bean_life_cycle.validation;

public class NotValidException extends RuntimeException {
    public NotValidException(String msString) {
        super(msString);
    }
}
