package com.core._03_static_and_final;

public class UsingStaticExample {
    public static String staticField = "Hello";

    public static void staticMethod() {
        System.out.println("Static method");
    }

    static {
        System.out.println("Public static block");
    }

    public UsingStaticExample() {
        System.out.println("Hàm main được gọi");
        System.out.println("truy cập vào static field" + staticField);
    }
}
