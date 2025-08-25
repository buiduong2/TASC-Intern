package com.core._02_string;

public class _04_CompareString {
    public static void main(String[] args) {
        String a1 = "Hello";
        String a2 = "Hello";
        String b1 = "World";
        String b2 = new String("World");
        String c = "WORLD";

        // 1. So sánh tham chiếu
        System.out.println(a1 == a2); // true - cùng tham chiếu do lấy từ pool
        System.out.println(b1 == b2); // false - sử dụng new khác pool

        // 2. So sánh nội dung
        System.out.println(a1.equals(b1)); // false - "Hello" !== "World"
        System.out.println(b1.equals(b2)); // true - "World" === "World"
        System.out.println(b2.equals(c)); // false "World" !== "WORLD"

        // So sánh ko phân biệt hoa thường
        System.out.println(b1.equalsIgnoreCase(c)); // true
        System.out.println(b1.equalsIgnoreCase(a1)); // false
    }

}
