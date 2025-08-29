package _01_creational._01_singleton;

public class ThreadSafeSingleton {

    // volatile để có thẻ khai báo một giá trị có thể được sử dụng bởi nhiều Thread
    // - tránh việc cache
    private static volatile ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {

    }

    // Sử dụng từ khóa synchornized ở cấp độ của static method
    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }

        return instance;
    }
}
