package _01_creational._01_singleton;

public class StaticBlockInitialization {

    // Static Block initialization for exception handling
    static {
        try {
            INSTANCE = new StaticBlockInitialization();
        } catch (Exception e) {
            throw new RuntimeException("Exception occured in creating isntance");
        }
        // Tiến hành việc code theo logic khởi tạo. Thêm thắt tài nguuyen

    }
    private static final StaticBlockInitialization INSTANCE;

    //
    private StaticBlockInitialization() {
        System.out.println("Hello StaticBlockInitialization");
    }

    public static final StaticBlockInitialization getInstance() {
        return INSTANCE;
    }

    public static int value = 1;

}
