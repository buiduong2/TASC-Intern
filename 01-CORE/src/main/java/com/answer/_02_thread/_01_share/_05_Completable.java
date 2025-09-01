package com.answer._02_thread._01_share;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class _05_Completable {

    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<String> future = new CompletableFuture<>();

        Thread t1 = new Thread(() -> {
            try {
                String mesage = future.get();
                System.out.println(mesage);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        Thread t2 = new Thread(() -> {
            future.complete("Hello from Thread 2");
        });

        t1.start();
        Thread.sleep(100);
        t2.start();
        // Hello from Thread 2

    }

}
