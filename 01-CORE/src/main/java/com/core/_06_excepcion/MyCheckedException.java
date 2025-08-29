package com.core._06_excepcion;

// Custom checked exception
public class MyCheckedException extends Exception {
    // Constructor mặc định
    public MyCheckedException() {
        super("Đây là một checked exception tự định nghĩa");
    }

    // Constructor với message
    public MyCheckedException(String message) {
        super(message);
    }

    // Constructor với message + cause
    public MyCheckedException(String message, Throwable cause) {
        super(message, cause);
    }
}