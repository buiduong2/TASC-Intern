package _01_creational._01_singleton.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class _04_ThreadSafe {

    public static class NonSafeThreadLazySingleton {
        private static NonSafeThreadLazySingleton instance;

        private NonSafeThreadLazySingleton() {

        }

        public static NonSafeThreadLazySingleton getInstance() {
            if (instance == null) {
                // Việc khởi tạo tốn thời gian
                System.out.println("Thread");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                instance = new NonSafeThreadLazySingleton();

            }
            return instance;
        }

    }

    public static void main(String[] args) {
        notUseDoubleCheckIsNonSafeThread();
    }

    /**
     * Sử dụng SIngleton lazy loại bình thường ko được triển khai cho Đa luồng
     */
    public static void notUseDoubleCheckIsNonSafeThread() {
        List<NonSafeThreadLazySingleton> instances = Collections.synchronizedList(new ArrayList<>());

        Thread t1 = new Thread(() -> instances.add(NonSafeThreadLazySingleton.getInstance()));
        Thread t2 = new Thread(() -> instances.add(NonSafeThreadLazySingleton.getInstance()));

        t2.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t1.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        NonSafeThreadLazySingleton i1 = instances.get(0);
        NonSafeThreadLazySingleton i2 = instances.get(1);

        System.out.println(i1 == i2); // false Vậy ta nhận được 2 Thread khác nhau. Có tỷ lệ trong đa luông

        // Như vậy đã có sự thay đổi xảy ra
    }

}
