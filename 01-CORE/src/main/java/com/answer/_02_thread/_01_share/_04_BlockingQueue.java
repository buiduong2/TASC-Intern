package com.answer._02_thread._01_share;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class _04_BlockingQueue {

    private static class SharedObject {
        public String message;
    }

    public static void main(String[] args) {
        SharedObject shareDObject = new SharedObject();
        BlockingQueue<SharedObject> queue = new LinkedBlockingQueue<>();
        Thread t1 = new Thread(() -> {
            try {
                SharedObject result = queue.take();
                System.out.println(result.message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        Thread t2 = new Thread(() -> {
            shareDObject.message = "Hello from Thread 2";
            queue.add(shareDObject);

        });
        t1.start();
        t2.start();
        // Hello from Thread 2

    }
}
