package com.core._03_static_and_final;

public class _02_Final {

    public static void main(String[] args) {
        UsingFinalExample example = new UsingFinalExample();
        System.out.println(example);
        // example.finalField = 5;
        // Error: final can't be asigned

    }

    /**
     * Demo thử override final method
     * 
     * - o thể overridde
     */
    public class SubClass extends UsingFinalExample {
        // Error can't not override final method
        // void finalMethod() {

        // }

    }

    /**
     * Demo extends final class
     * 
     * - Không thể extends Final Class
     */

    // public class SubClass2 extends UsingFinalExample.FinalClass {
    // Error : cannot subclass the final class
    // }
}
