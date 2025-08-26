package com.thread._02_thread;

public class _01_CreateThread {

    public static void main(String[] args) {
        // runExtendedThread();
        runThreadWithRunnable();
    }

    public static void runTask(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("Thread: " + threadName + " , " + i);
            try {
                // Cho luồng chạy chậm để mô phỏng đa luồng
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread: " + threadName + " , END");
    }

    /**
     * TH1: tạo Thread bằng cách extends Thread
     */
    static class ThreadExample extends Thread {

        // Cần oVerride method này để nó chạy trong môi trường đa luồng
        private String threadName;

        public ThreadExample(String threadName) {
            this.threadName = threadName;
        }

        @Override
        public void run() {
            runTask(threadName);
        }

        public String getThreadName() {
            return threadName;
        }

    }

    /**
     * Để create Thread từ 2 luồng khác nhau
     */
    public static void runExtendedThread() {
        Thread thread1 = new ThreadExample("Thread-1");
        Thread thread2 = new ThreadExample("Thread-2");

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("END");
        // Ta có thể thây thứ in được đan xen giữa Thread1 và Thread2. Vậy chúng chạy
        // song song

        // Thread: Thread-2 , 0
        // Thread: Thread-1 , 0
        // Thread: Thread-1 , 1
        // Thread: Thread-2 , 1
        // Thread: Thread-2 , 2
        // Thread: Thread-1 , 2
        // Thread: Thread-2 , 3
        // Thread: Thread-1 , 3
        // Thread: Thread-2 , 4
        // Thread: Thread-1 , 4
        // Thread: Thread-1 , END
        // Thread: Thread-2 , END
        // END

    }

    /**
     * Demo Create Thread by implmeneting interface Runnable
     */

    public static class ExampleThread implements Runnable {

        private String threadName;

        public ExampleThread(String threadName) {
            this.threadName = threadName;
        }

        // Implmeneted method run
        @Override
        public void run() {
            runTask(threadName);
        }

    }

    public static void runThreadWithRunnable() {
        Thread t1 = new Thread(new ExampleThread("Thread-1"));// Tạo Thread bằng contrucstor gửi vào một Example THread
        Thread t2 = new Thread(new ExampleThread("Thread-2"));

        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("END");

        // 2 Thread cũng chạy song song với nhau
        // Thread: Thread-1 , 0
        // Thread: Thread-2 , 0
        // Thread: Thread-1 , 1
        // Thread: Thread-2 , 1
        // Thread: Thread-2 , 2
        // Thread: Thread-1 , 2
        // Thread: Thread-1 , 3
        // Thread: Thread-2 , 3
        // Thread: Thread-1 , 4
        // Thread: Thread-2 , 4
        // Thread: Thread-1 , END
        // Thread: Thread-2 , END
    }
}
