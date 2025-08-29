package com.thread._01_synchronize_asynchronous;

public class _03_SynchronizedBlock {
    public static void main(String[] args) {
        // useDifferentResourceNonBlock();
        // useSameResourceMustBlock();
        useSameResourceButDiffrentCode();
    }

    public static void task(Object resource, String threadName) {

        for (int i = 0; i < 5; i++) {
            System.out.println("Out of Synchronized Block : " + threadName + ", " + i);
        }

        synchronized (resource) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 5; i++) {
                System.out.println("Thread " + threadName + " , " + i);
            }
        }
    }

    public static void task2(Object resource, String threadName) {

        for (int i = 0; i < 5; i++) {
            System.out.println("Out of Synchronized Block : " + threadName + ", " + i);
        }

        synchronized (resource) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 5; i++) {
                System.out.println("Thread " + threadName + " , " + i);
            }
        }
    }

    /**
     * Sử dụng các instance khác nhau vào trong Synchornized xem chúng có block
     * Thread không
     * 
     * NX:
     * 
     * - Block syncrhonized giống như một câu lệnh if. nó sẽ xác định tài nguyên để
     * khóa và tài nguyên để kiểm tra
     * 
     */
    public static void useDifferentResourceNonBlock() {

        Thread t1 = new Thread(() -> task("Resource", "A"));

        Thread t2 = new Thread(() -> task("Other Resource", "B"));

        t1.start();
        t2.start();

        // Bất đồng bộ
        // Thread A , 0
        // Thread A , 1
        // Thread B , 0
        // Thread B , 1
        // Thread B , 2
        // Thread B , 3
        // Thread A , 2
        // Thread A , 3
        // Thread B , 4
        // Thread A , 4

        // Chúng chạy Song song
    }

    public static void useSameResourceMustBlock() {
        // StringPool
        Thread t1 = new Thread(() -> task("Resource", "A"));
        Thread t2 = new Thread(() -> task("Resource", "B"));

        t1.start();
        t2.start();

        // Chúng đồng bộ
        // Thread A , 0
        // Thread A , 1
        // Thread A , 2
        // Thread A , 3
        // Thread A , 4
        // Thread B , 0
        // Thread B , 1
        // Thread B , 2
        // Thread B , 3
        // Thread B , 4
    }

    /**
     * Sử dụng chung một Resource. Nhưng lúc này ta sử dụng các đoạn code khác nhau
     * 
     * - Chứng minh chỉ cần vào trong một đoạn code yêu cầu Object thì sẽ phải đợi.
     * KO quan trọng đoạn nào trong code
     */
    public static void useSameResourceButDiffrentCode() {
        Thread t1 = new Thread(() -> task("Resource", "A"));
        // Sử dụng task2
        Thread t2 = new Thread(() -> task2("Resource", "B"));

        t1.start();
        t2.start();

        // Bên ngoài các Synchoznied Blocjk. Các thread hoạt động song song bình thường
        // Nhưng khi vào BLock thì nó được đồng bộ

        // Out of Synchronized Block : A, 0
        // Out of Synchronized Block : B, 0
        // Out of Synchronized Block : A, 1
        // Out of Synchronized Block : B, 1
        // Out of Synchronized Block : B, 2
        // Out of Synchronized Block : A, 2
        // Out of Synchronized Block : B, 3
        // Out of Synchronized Block : A, 3
        // Out of Synchronized Block : B, 4
        // Out of Synchronized Block : A, 4
        // Thread B , 0
        // Thread B , 1
        // Thread B , 2
        // Thread B , 3
        // Thread B , 4
        // Thread A , 0
        // Thread A , 1
        // Thread A , 2
        // Thread A , 3
        // Thread A , 4
    }
}
