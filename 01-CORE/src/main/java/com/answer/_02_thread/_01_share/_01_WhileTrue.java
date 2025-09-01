package com.answer._02_thread._01_share;

/**
 * Cách để giải tiếp giữa các thread
 * 
 * _ cách 1: While true
 */
public class _01_WhileTrue {

    public static void main(String[] args) {
        SharedObject sharedObject = new SharedObject();

        Thread t1 = new Thread(() -> {
            while (sharedObject.getMessage() == null) {
            }
            System.out.println(sharedObject.getMessage());
        });
        Thread t2 = new Thread(() -> {
            sharedObject.setMessage("hello From Thread 2");
        });
        t1.start();
        t2.start();
        // hello From Thread 2

    }

    private static class SharedObject {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }
}
