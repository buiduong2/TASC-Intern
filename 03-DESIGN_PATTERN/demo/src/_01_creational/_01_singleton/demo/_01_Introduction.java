package _01_creational._01_singleton.demo;

import _01_creational._01_singleton.EnumSingleton;

public class _01_Introduction {
    public static void main(String[] args) {
        enumSingleton();
    }

    /**
     * Singleton của Enum
     * 
     * - từ log ta có thể thấy constructor của nó chỉ được khởi tạo 1 lần duy nhất
     * 
     * - Và 2 lần gọi trả về cùng 1 đối tượng (cùng chugn tham chiếu)
     * 
     * - Có thể có field và method
     * 
     * - Cũng có thể implmenet interface
     */
    public static void enumSingleton() {
        EnumSingleton instance = EnumSingleton.INSTANCE;
        EnumSingleton instance2 = EnumSingleton.INSTANCE;
        System.out.println("Nó có phải là độc nhất không : " + (instance == instance2));

        // Enum Constructor ???c g?i - ???c kh?i t?o - chỉ được gọi duy nhất 1 lần
        // Nó có ph?i là ??c nh?t không : true

        // Có thuộc tính và method
        instance.setValue(100);
        System.out.println(instance2.getValue()); // 100 - Đã bị ảnh hưởng bởi sự thay đổi của instance1

        // Có thể implement interface
        Runnable runnable = instance;
        runnable.run();
        // ?ây là method ?a hình
    }


}
