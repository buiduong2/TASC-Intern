package _01_creational._01_singleton.demo;

import _01_creational._01_singleton.BillPughSingleton;
import _01_creational._01_singleton.EnumSingleton;
import _01_creational._01_singleton.StaticBlockInitialization;

public class _02_EagerDemo {

    public static void main(String[] args) {
        staticBLockSingleton();
        // innerHelperClassIsLazy();
        // enumNotCreateBeforeLoad();
    }

    /**
     * Static blokc SIngleton là một eager Initializatoin
     * - Vậy là việc nạp class chỉ diễn ra khi nó thực sự tương tác với method hoặc
     * field nào đó. Không phải chỉ gọi class.class là đủ
     */
    public static void staticBLockSingleton() {
        System.out.println("Bắt đàu chương trình");
        System.out.println("Bắt đàu khởi tạo khi class được load");
        System.out.println(StaticBlockInitialization.class);
        System.out.println("Bắt đầu gọi method");
        StaticBlockInitialization instance = StaticBlockInitialization.getInstance();
        StaticBlockInitialization instance2 = StaticBlockInitialization.getInstance();

        System.out.println("Sau khi gọi");
        System.out.println(instance == instance2);

        // B?t ?àu ch??ng trình
        // B?t ?àu kh?i t?o khi class ???c load
        // class _01_creational._01_singleton.StaticBlockInitialization /// giọ đến
        // class thôi chưa đủ
        // B?t ??u g?i method // khi gọi đến method mới bắt đầu nạp class
        // Hello StaticBlockInitialization
        // Sau khi g?i
        // true
    }

    /**
     * Chưa nhắc đến việc JVM đọc đén dòng code chứa class thì nạp. Mà khi đọc đến
     * class này cũng k nạp. Chỉ khi chúng ta gọi method thì nạp inner
     */
    public static void innerHelperClassIsLazy() {
        System.out.println("Chưa khởi tạo");
        // bên trong mới gọi đến class cần gọi đến field static mới load
        BillPughSingleton instance1 = BillPughSingleton.getInstance();
        BillPughSingleton instance2 = BillPughSingleton.getInstance();
        System.out.println("Đã khởi tạo");
        System.out.println(instance1 == instance2);

        // Ch?a kh?i t?o
        // Hello BillPughSingleton
        // ?? kh?i t?o
        // true
    }

    /**
     * Enum chỉ khi JVM đọc đến dòng code gọi đến tên class của Enum thì nó mơi thực
     * sự được load vào trong JVM và được khởi tạo
     * 
     * - gần như là Lazy
     */
    public static void enumNotCreateBeforeLoad() {

        System.out.println("Ở chỗ này Enum chưa được khởi tạo");

        System.out.println(EnumSingleton.INSTANCE); // Đọc đến dòng này thì constructor mới được gọi

        System.out.println("Ở chỗ này thì Enum đã được khởi tạo");

        // ? ch? này Enum ch?a ???c kh?i t?o
        // "Enum Constructor được gọi - được khởi tạo"
        // INSTANCE
        // ? ch? này thì Enum ?? ???c kh?i t?o
    }
}
