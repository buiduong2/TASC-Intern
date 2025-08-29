package _01_creational._01_singleton;

public class EagerInitializedSingleton {
    private static final EagerInitializedSingleton INSTANCE = new EagerInitializedSingleton();

    // Sử dụng private constructor.
    // Như vậy bên ngoài class sẽ ko thể khởi tạo instance bằng constructor nưa
    private EagerInitializedSingleton() {
    }

    public static EagerInitializedSingleton getInstance() {
        return INSTANCE;
    }
}
