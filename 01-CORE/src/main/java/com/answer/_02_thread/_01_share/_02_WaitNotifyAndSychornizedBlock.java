package com.answer._02_thread._01_share;

/**
 * Cách 2 sử dụng Syncrhonized block + notify()
 */
public class _02_WaitNotifyAndSychornizedBlock {

    private static class SharedObject {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

    }

    public static void main(String[] args) {
        SharedObject sharedObject = new SharedObject();
        Object lock = new Object();

        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                while (sharedObject.getMessage() == null) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(sharedObject.getMessage());
            }
        });
        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                sharedObject.setMessage("hello From Thread 2");
                lock.notifyAll();
            }
        });
        t1.start();
        t2.start();
        // hello From Thread 2 

    }
}