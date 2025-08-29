package com.core._03_static_and_final;

public class _01_Static {
    public static String WebSite = "helloworld.com";

    public static void main(String[] args) {
        
        // static Block in: Public static block
        
        // public Static block sẽ luôn luôn được gọi khi lần đầu Java nạp các class vào
        // hệ thống

        // Static method
        UsingStaticExample.staticMethod(); // Có thể gọi trực tiếp method bằng class
        System.out.println(UsingStaticExample.staticField); // Có thể truy cập field thông qua class

        // Một số static method có sẵn
        System.out.println(Math.max(1, 2)); // Method tiện ích tìm max : KQ 2

    }
}
