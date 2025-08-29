package com.thread._01_synchronize_asynchronous;

public class _02_SynchronizedMethod {
    public static void main(String[] args) {
        // runTwoInstance();
        // shareInstanceWithSameSyncMethod();
        // shareInstanceDifferentSyncMethod();
        shareInstanceWithNormalMethodAndSyncMethod();
    }

    /**
     * Kiểm tra xem có phải 2 Instance khác nhau thì chúng ko liên quan đến nhau khi
     * sync không
     * 
     * Như ta đã biết khi một thread vào tỏng block sync thì nó sẽ chiếm hữu
     * instance đó. Cho đến khi thoát mới bỏ ra
     */
    public static void runTwoInstance() {
        var instance = new _02_SynchronizedMethod();
        var instance2 = new _02_SynchronizedMethod();

        Thread thread1 = new Thread(() -> instance.syncMethod1("A"));
        Thread thread2 = new Thread(() -> instance2.syncMethod1("B"));
        thread1.start();
        thread2.start();

        // Mong đợi. Hoạt động song song. Vì ko có chung tài nugyene khóa
        // Method: 1 , Thread: B , 0
        // Method: 1 , Thread: B , 1
        // Method: 1 , Thread: A , 0
        // Method: 1 , Thread: B , 2
        // Method: 1 , Thread: B , 3
        // Method: 1 , Thread: A , 1
        // Method: 1 , Thread: B , 4
        // Method: 1 , Thread: A , 2
        // Method: 1 , Thread: A , 3
        // Method: 1 , Thread: A , 4

        // Chúng thực sự hoạt động song song. Synchornized ko ảnh hưởng đến cách chạy
        // chuowgn trình

    }

    /**
     * Lúc này ta sẽ chạy 2 thread cùng truy cập vào mọt obj. Và cùng sử dụng một
     * method sync
     * 
     * - Khi vào trong một Synchronized method. Monitor sẽ chiếm hữu this của class
     * chứa method đó
     * - Lúc này nếu có một Thread cần đến tài nguyên bị minitor chiếm hữu thì sẽ
     * phải đợi cho đến khi tìa nguyên đó được giải phóng
     */
    public static void shareInstanceWithSameSyncMethod() {
        var obj = new _02_SynchronizedMethod();
        Thread t1 = new Thread(() -> obj.syncMethod1("A"));
        Thread t2 = new Thread(() -> obj.syncMethod1("B"));
        t1.start();
        t2.start();

        /**
         * Mong đợi. Chỉ khi Chạy hết THread A thì mới chạy Thread B.
         * 
         * - Vì khi một Thread đi vào tỏng một Synchronized method monitor sẽ chiếm hữu
         * obj đó. Các thread khác ko thể lấy tài nguyên sẽ bị block để đợi
         */
        // Method: 1 , Thread: A , 0
        // Method: 1 , Thread: A , 1
        // Method: 1 , Thread: A , 2
        // Method: 1 , Thread: A , 3
        // Method: 1 , Thread: A , 4
        // Method: 1 , Thread: B , 0
        // Method: 1 , Thread: B , 1
        // Method: 1 , Thread: B , 2
        // Method: 1 , Thread: B , 3
        // Method: 1 , Thread: B , 4

        // 2 Thread chạy đồng bộ
    }

    /**
     * Sử dụng 2 Sync method khác nhau
     * 
     * - Chỉ cần vào trong một Sychronied method thì nó sẽ kiểm tra tài nugyene. Ko
     * cần phải là cùng một method cùng một dòng code mới block
     */
    public static void shareInstanceDifferentSyncMethod() {
        var obj = new _02_SynchronizedMethod();
        Thread t = new Thread(() -> obj.syncMethod1("A"));
        Thread t2 = new Thread(() -> obj.syncMethod2("A")); // Diffrent Sync Method

        t.start();
        t2.start();

        // Method: 1 , Thread: A , 0
        // Method: 1 , Thread: A , 1
        // Method: 1 , Thread: A , 2
        // Method: 1 , Thread: A , 3
        // Method: 1 , Thread: A , 4
        // Method: 2 , Thread: A , 0
        // Method: 2 , Thread: A , 1
        // Method: 2 , Thread: A , 2
        // Method: 2 , Thread: A , 3
        // Method: 2 , Thread: A , 4

        // Chúng thực sự chạy đồng bộ
    }

    /**
     * Lúc này ta sẽ test trường hợp . Liệu khi ko vào các block SYnchornized thì nó
     * có kiểm tra obj trong monitor hay không
     * 
     * NX :
     * 
     * - Chỉ khi gặp phải trường hợp Synchronzied thì chúng mới thực sự đòi hỏi tài
     * nguyên từ monitor lúc đó mới xác định dừng hay không. Còn các block bình
     * thường ko quan tâm
     * 
     */
    public static void shareInstanceWithNormalMethodAndSyncMethod() {
        var obj = new _02_SynchronizedMethod();
        Thread t = new Thread(() -> obj.syncMethod1("A"));
        Thread t2 = new Thread(() -> obj.normalMethod("B"));

        t.start();
        t2.start();
        // Chúng chạy song song
        // Method: NORMAL , Thread: B , 0
        // Method: NORMAL , Thread: B , 1
        // Method: 1 , Thread: A , 0
        // Method: NORMAL , Thread: B , 2
        // Method: 1 , Thread: A , 1
        // Method: NORMAL , Thread: B , 3
        // Method: 1 , Thread: A , 2
        // Method: 1 , Thread: A , 3
        // Method: 1 , Thread: A , 4
        // Method: NORMAL , Thread: B , 4
    }

    public void normalMethod(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("Method: NORMAL , Thread: " + threadName + " , " + i);
        }
    }

    public synchronized void syncMethod1(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("Method: 1 , Thread: " + threadName + " , " + i);
        }
    }

    public synchronized void syncMethod2(String threadName) {
        for (int i = 0; i < 5; i++) {
            System.out.println("Method: 2 , Thread: " + threadName + " , " + i);
        }
    }
}
