package com.thread._02_thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class _02_CheckThreadAlive {

    public static void main(String[] args) {
        // isAliveMethod();
        // joinMethod();
        // executorService();
        // completableFutureGet();
        completableFutureCallback();
    }

    public static void slowTask(String threadName) {
        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Slow task-" + threadName + " : " + i);
        }
    }

    public static void quickTask(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("Quick task-" + threadName + " : " + i);
        }
    }

    /**
     * Class Thread có một method là isAlive():boolean để kiểm tra một Thread đã
     * dừng hoạt động hay chưa
     * 
     * - Khi một Thread đã kết thúc isAlive() sẽ trả về true. Và ngược lại
     */
    public static void isAliveMethod() {
        Thread t = new Thread(() -> slowTask("A"));

        t.start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        quickTask("MAIN");

        System.out.println("MAIN thread has done TASK");

        while (t.isAlive()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Wait child-thread");
        }

        // 2 thread làm việc song song với nhau.
        // Nhưng Main sẽ đợi cho đến khi thread kia kết thúc (while true) thì mới tiếp
        // tục chạy
        // Slow task-MAIN : 0
        // Slow task-MAIN : 1
        // Slow task-A : 0
        // Slow task-MAIN : 2
        // Slow task-MAIN : 3
        // Slow task-MAIN : 4
        // MAIN thread has done TASK
        // Wait child-thread
        // Wait child-thread
        // Slow task-A : 1
        // Wait child-thread
        // Wait child-thread
        // Wait child-thread
        // Slow task-A : 2
        // Wait child-thread
        // Wait child-thread
        // Wait child-thread
        // Slow task-A : 3
        // Wait child-thread
        // Wait child-thread
        // Wait child-thread
        // Slow task-A : 4
        // Wait child-thread
        // All Done. END

        System.out.println("All Done. END");
    }

    public static void joinMethod() {
        Thread t = new Thread(() -> slowTask("A"));

        t.start();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        quickTask("MAIN");

        System.out.println("MAIN thread has done TASK");
        System.out.println("MAIN thread WAIT ...");
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Main thread done");

        // Chúng chạy song song.
        // nhưng main thread sẽ phải đợi thread kia hoàn tất tại đoạn code t.join();
        // Slow task-A : 0
        // Slow task-MAIN : 0
        // Slow task-MAIN : 1
        // Slow task-MAIN : 2
        // Slow task-MAIN : 3
        // Slow task-MAIN : 4
        // MAIN thread has done TASK
        // MAIN thread WAIT ...
        // Slow task-A : 1
        // Slow task-A : 2
        // Slow task-A : 3
        // Slow task-A : 4
        // Main thread done
    }

    /**
     * Ta có thể sử dụng .get() để block thread hiện tại cho đến khi chilThread kết
     * thúc
     * 
     * - Hoặc sử dụng shutdown() khi tất cả các thread đều kết thúc để tiếp tục chạy
     * thread đó
     */
    public static void executorService() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // Sử dụng với Future
        Future<Integer> future = executorService.submit(() -> {
            slowTask("Future-1");
            return 0;
        });
        Future<Integer> future2 = executorService.submit(() -> {
            slowTask("Future-2");
            return 0;
        });

        System.out.println("TTrước khi kết thúc");

        System.out.println("Thread done: " + future.isDone());

        System.out.println("Tiến hành đợi cho đến khi Thread kết thúc");
        int result = -1;
        try {
            result = future.get();
            future2.get();
            System.out.println("Thread Child End");
            System.out.println("RESULT : " + result);
            System.out.println("Future-1 is alive: " + future.isDone());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("Thread Main END");
        executorService.shutdown();

        // TTr??c khi k?t thúc
        // Thread done: false
        // Ti?n hành ??i cho ??n khi Thread k?t thúc
        // Slow task-Future-2 : 0
        // Slow task-Future-1 : 0
        // Slow task-Future-2 : 1
        // Slow task-Future-1 : 1
        // Slow task-Future-1 : 2
        // Slow task-Future-2 : 2
        // Slow task-Future-1 : 3
        // Slow task-Future-2 : 3
        // Slow task-Future-1 : 4
        // Slow task-Future-2 : 4
        // Thread Child End
        // RESULT : 0
        // Future-1 is alive: true
        // Thread Main END

    }

    /**
     * Sử dụng CompletableFuture.
     * 
     * - Sử dụng hmaf get() hoặc join() sẽ khiến cho parent thread sẽ bị block lại
     * cho đến khi childThread kết thúc hoặc trả về kết quả
     */
    public static void completableFutureGet() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            slowTask("RETURN");
            return "Hello CompletableFuture";
        });

        // Sử dụng như một Thread với voic trả về
        CompletableFuture<Void> futureVoid = CompletableFuture.runAsync(() -> slowTask("VOID"));
        String result = null;

        try {
            // future.join();// Hoàn toàn hỗ trợ join() giống thread
            result = future.get();
            futureVoid.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        // Slow task-RETURN : 0
        // Slow task-VOID : 0
        // Slow task-RETURN : 1
        // Slow task-VOID : 1
        // Slow task-RETURN : 2
        // Slow task-VOID : 2
        // Slow task-RETURN : 3
        // Slow task-VOID : 3
        // Slow task-RETURN : 4
        // Slow task-VOID : 4
        // T?t c? k?t thúc KQ: Hello CompletableFuture

        System.out.println("Tất cả kết thúc KQ: " + result);
    }

    /**
     * Sử dụng CompletableFuture. Nhưng dùng callback
     * 
     * - Sử dụng một
     */
    public static void completableFutureCallback() {

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            slowTask("Child");
            return "Hello CompletableFuture";
        });

        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Parent Task -1 : " + i);
        }

        CompletableFuture<Void> lastFuture = future.thenAccept((msg) -> {
            quickTask("Parent-2");

            System.out.println("Tất cả kết thúc KQ: " + msg);
        });

        lastFuture.join(); // Cần phải đợi nếu ko sẽ bị Trình dọn rác rọn mất

        // Lúc đầu Slow Task parent-1 và Child sẽ hoạt động song song
        // Nhưng sau khi QuickTask-2 sẽ phải đợi cho đến khi child-task kết thúc (sau
        // khi kết thúc mới chạy cái Runnable của chúng ta)
        // CUối cùng phải Join() thì parent Thread mới kết thúc
        // Parent Task -1 : 0
        // Slow task-Child : 0
        // Parent Task -1 : 1
        // Slow task-Child : 1
        // Parent Task -1 : 2
        // Slow task-Child : 2
        // Slow task-Child : 3
        // Slow task-Child : 4
        // Quick task-Parent-2 : 0
        // Quick task-Parent-2 : 1
        // Quick task-Parent-2 : 2
        // Quick task-Parent-2 : 3
        // Quick task-Parent-2 : 4
        // T?t c? k?t thúc KQ: Hello CompletableFuture
    }

}
