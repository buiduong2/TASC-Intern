package _01_creational._01_singleton;

public class BillPughSingleton {
    // Class Singleton không lưu trữ tham chiếu của Singleton nữa

    private BillPughSingleton() {
        System.out.println("Hello BillPughSingleton");

    }

    public static BillPughSingleton getInstance() {
        return SingletonHelper.INSTANCE;
    }

    // Inner static private class lại lưu trữ tham chiếu đến instance
    private static class SingletonHelper {
        private static final BillPughSingleton INSTANCE = new BillPughSingleton();
    }

}