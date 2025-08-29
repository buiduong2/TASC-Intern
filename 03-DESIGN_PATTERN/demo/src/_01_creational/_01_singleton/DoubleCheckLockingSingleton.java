package _01_creational._01_singleton;

public class DoubleCheckLockingSingleton {
    private static volatile DoubleCheckLockingSingleton instance;

    private DoubleCheckLockingSingleton() {

    }

    public static DoubleCheckLockingSingleton getInstance() {
        // Làm gì đó trước khi lấy instance

        // không syncrhonzied ở đây luôn vì nếu đã khởi tạo rồi thì đa luồng hay không chỗ này không còn quan trọng nữa
        // nếu ta cố gắng synchornized từ chỗ này thì mọi truy cập vào sẽ bị chặn không cần thiết
        if (instance == null) {
            // Làm các công việc trước khi khởi tọa instance...
            // Khóa các thread khác không thể đi vào trong trong khi khởi tạo
            synchronized (DoubleCheckLockingSingleton.class) {
                // Kiểm tra một lần nữa. Có thể các thread khác đã khởi tạo trước đó
                if (instance == null) {
                    instance = new DoubleCheckLockingSingleton();
                }
            }
        }

        return instance;
    }
}
