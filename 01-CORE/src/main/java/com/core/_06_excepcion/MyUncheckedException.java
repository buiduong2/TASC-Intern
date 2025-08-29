package com.core._06_excepcion;

// Custom unchecked exception
public class MyUncheckedException extends RuntimeException {
    public MyUncheckedException() {
        super("Đây là một unchecked exception tự định nghĩa");
    }

    public MyUncheckedException(String message) {
        super(message);
    }
}