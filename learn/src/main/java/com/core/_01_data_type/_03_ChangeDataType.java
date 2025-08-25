package com.core._01_data_type;

public class _03_ChangeDataType {
    public static void main(String[] args) {
        // autoboxing();
        // unboxing();
        // unboxingNullWrapper();
    }

    /**
     * Demo Việc tự động chuyển đổi từ Primitive -> Object
     */
    public static void autoboxing() {
        int x = 10;
        Integer y = x; // Gán từ int sang INteger
        Double d = 3.14; // Khái báo Literal giống primitive

        System.out.println(y); // 10
        System.out.println(d); // 3.14

        // Use method
        System.out.println(y.doubleValue()); // 10.0
        System.out.println(y.compareTo(11)); // -1 (nhỏ hơn)
    }

    /**
     * Demo tự động chuyển đổi từ Wrapper -> Primitive
     */
    public static void unboxing() {
        Integer a = 20;
        int b = a; // Tự động chuyển đổi thành primitive khi gán
        int sum = a + 5; // Tự động chuyển đổi thành primitive khi thực hiện các phép toán

        System.out.println(b); // 20
        System.out.println(sum); // 25
    }

    /**
     * Demo chuyển đổi từ Wrapper có gia trị = null
     * // Sẽ throw ra exception
     */
    @SuppressWarnings("null")
    public static void unboxingNullWrapper() {
        Integer a = null;
        try {
            int b = a;
            System.out.println("b: " + b);
        } catch (NullPointerException e) {
            System.out.println("NullPointerException occur"); // NullPointerException occur
        }
    }
}
