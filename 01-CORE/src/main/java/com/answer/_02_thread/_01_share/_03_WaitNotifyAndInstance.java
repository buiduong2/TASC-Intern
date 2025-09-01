package com.answer._02_thread._01_share;

/**
 * Instance tự có đa chức năng vừa kiểm soát việc thêm giữa các luồng vừa lưu dữ
 * liệu các lường
 */
public class _03_WaitNotifyAndInstance {

    private static class SharedObject {
        private String message;

        public synchronized void setMessage(String message) {
            this.message = message;
            notifyAll();
        }

        public synchronized String getMessage() {
            while (message == null) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return message;
        }
    }

    public static void main(String[] args) {
        SharedObject sharedObject = new SharedObject();
        Thread t1 = new Thread(() -> {

            System.out.println(sharedObject.getMessage());
        });
        Thread t2 = new Thread(() -> {
            sharedObject.setMessage("hello From Thread 2");
        });
        t1.start();
        t2.start();
        // hello From Thread 2
    }

}
