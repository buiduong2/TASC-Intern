package _01_creational._01_singleton;

// Hoàn toàn có thể sơ hữu tính đa hình
public enum EnumSingleton implements Runnable {
    // Về lý thuyết đây cũng là LAZY
    // Khi mà chúng ta chưa chạm đến EnumSingleton.someThing thì nó chưa được khởi
    // tạo
    INSTANCE;

    private EnumSingleton() {
        System.out.println("Enum Constructor được gọi - được khởi tạo");
    }

    private int value = 0;

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public void doSomething() {
        System.out.println("Enum Singleton is working! Current Value: =" + value);
    }

    @Override
    public void run() {
        System.out.println("Đây là method đa hình");
    }
}
