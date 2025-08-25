package com.core._01_data_type;

public class _05_DefaultValue {

    public static class CustomClass {
        private double gpa;
        private String name;

        public double getGpa() {
            return gpa;
        }

        public String getName() {
            return name;
        }

    }

    // Static field
    static int number;
    static boolean flag;
    static char letter;
    static String text;
    static Integer wrapperInt;
    static CustomClass custom;
    static int[] arr;

    /**
     * 
     * Demo Giá trị mặc định của các variable (instance, static, localvairable)
     * 
     * - Field instane và field static giống nhau sẽ tự động tạo ra các giá trị mặc
     * định
     * - Số = 0 , boolean = false, char = kis tuwj Unicode 0 . . Object = null
     * 
     * - Còn localvariable sẽ ko thê rsuwr dụng trước khi gán
     */
    public static void main(String[] args) {

        // Static field variable
        System.out.println(number); // 0
        System.out.println(flag); // false
        System.out.println(letter); //
        System.out.println(text); // null
        System.out.println(wrapperInt);// null
        System.out.println(custom); // null
        System.out.println(arr);// null

        // localVariable bắt buộc phải gán giá trị trước khi dugf
        // int localInt;
        // System.out.println(localInt);
        // error: The local variable localIny may not have been inlitized

        // Field Variable (instance)
        CustomClass class1 = new CustomClass();
        System.out.println(class1.getGpa()); // 0.0
        System.out.println(class1.getName()); // null
    }
}
