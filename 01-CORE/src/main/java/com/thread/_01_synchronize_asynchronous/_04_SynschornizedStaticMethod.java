package com.thread._01_synchronize_asynchronous;

public class _04_SynschornizedStaticMethod {

    public synchronized void task(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("INSTANCE SYNC === Thread " + threadName + " , " + i);
        }
    }

    public static void staticTaskAync(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("STATIC ASYNC === Thread " + threadName + " , " + i);
        }
    }

    public static synchronized void staticTaskSync1(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("STATIC SYNC1 === Thread " + threadName + " , " + i);
        }
    }

    public static synchronized void staticTaskSync2(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("STATIC SYNC2 === Thread " + threadName + " , " + i);
        }
    }

    public static void main(String[] args) {
        // instanceSyncVSStaticSync();
        staticSyncWithDifferentMethod();
    }

    /**
     * Khi sử dụng static synchronize thì object bị chiếm giữ sẽ là Object Class
     * 
     * - Không liên quan gì đến instance . Object của instance và class là khác nhau
     */
    public static void instanceSyncVSStaticSync() {
        var obj = new _04_SynschornizedStaticMethod();
        Thread t1 = new Thread(() -> obj.task("A"));
        Thread t2 = new Thread(() -> staticTaskSync1("B"));

        t1.start();
        t2.start();

        // Chúng bất đồng bộ .
        // STATIC SYNC1 === Thread B , 0
        // INSTANCE SYNC === Thread A , 0
        // INSTANCE SYNC === Thread A , 1
        // STATIC SYNC1 === Thread B , 1
        // INSTANCE SYNC === Thread A , 2
        // STATIC SYNC1 === Thread B , 2
        // INSTANCE SYNC === Thread A , 3
        // INSTANCE SYNC === Thread A , 4
        // STATIC SYNC1 === Thread B , 3
        // STATIC SYNC1 === Thread B , 4
    }

    /**
     * Chứng minh việc kiểm tra chiếm hữu của ObjectClass
     */
    public static void staticSyncWithDifferentMethod() {
        Thread t1 = new Thread(() -> staticTaskSync2("A"));
        Thread t2 = new Thread(() -> staticTaskSync1("B"));

        t1.start();
        t2.start();

        // Chúng đồng bộ Thread B giải phóng -> Thread A
        // STATIC SYNC1 === Thread B , 0
        // STATIC SYNC1 === Thread B , 1
        // STATIC SYNC1 === Thread B , 2
        // STATIC SYNC1 === Thread B , 3
        // STATIC SYNC1 === Thread B , 4
        // STATIC SYNC2 === Thread A , 0
        // STATIC SYNC2 === Thread A , 1
        // STATIC SYNC2 === Thread A , 2
        // STATIC SYNC2 === Thread A , 3
        // STATIC SYNC2 === Thread A , 4
    }
}
