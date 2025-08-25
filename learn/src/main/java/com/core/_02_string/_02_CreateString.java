package com.core._02_string;

public class _02_CreateString {

    public static void main(String[] args) {

        // 1. Literal
        String s1 = "Hello World";
        System.out.println(s1);// Hello World

        // 2.new
        String s2 = new String("Hello World");
        System.out.println(s2);// Hello World

        // 3. from character array
        char[] chars = new char[] { 'H', 'e', 'l', 'l', 'o', ' ', 'w', 'o', 'r', 'l', 'd' };
        String s3 = new String(chars);
        System.out.println(s3);

        // 4. From Utilties method
        String s4 = String.valueOf(123);
        String s5 = String.valueOf(true);
        System.out.println(s4);// "123"
        System.out.println(s5);// "true"

        // 5. String Builder
        StringBuilder sb = new StringBuilder("Hello");
        sb.append(" World");
        String s6 = sb.toString();
        System.out.println(s6); // Hello World

        // Concat literal String
        String s7 = 123 + "Hello World";
        String s8 = true + "false";
        System.out.println(s7); // 123Hello World
        System.out.println(s8); // truefalse

    }
}
