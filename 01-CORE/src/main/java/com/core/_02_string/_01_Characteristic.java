package com.core._02_string;

import java.util.Arrays;

/**
 * Đặc điểm tính chất của String
 */
public class _01_Characteristic {

    public static void main(String[] args) {
        // stringIsImmutable();
        // stringIsStoredInStringPool();
        // stringCanCompareWithEqual();
        // stringHasMethods();
        // stringCanTransferToArrayAndReverse();
        // stringCanConcatWithOperandPlus();
        stringImplementedComparable();
    }

    /**
     * 1. Tính bất biến
     */
    public static void stringIsImmutable() {
        // Nối chuỗi với String
        String s1 = "hello";
        String s2 = s1.concat(" world");

        System.out.println(s1); // hello
        System.out.println(s2); // hello World

        // Nỗi chuỗi bằng phép +
        String s3 = "Hello";
        String s4 = s3;
        s3 += " world";

        System.out.println(s3); // Hello world // là kết quả của phép nối
        System.out.println(s4); // Hello - Giữ nguyên giá trị
        // Trừ khi chúng ta thay đổi thuộc tính. Còn việc gán lại bằng các toán tử là
        // tạo ra đối tượng mới

    }

    /**
     * Kiểm tra xem String có được lưu trong StringPool không
     */
    public static void stringIsStoredInStringPool() {

        // Khi khai báo Literal lại
        String a = "java";
        String b = "java";
        String b2 = "javascript";
        System.out.println(a == b); // true - Cùng trỏ về một ô nhớ - do String là Object lưu địa chỉ ô nhớ
        System.out.println(a.equals(b));// true - Nội dung giống nhau
        System.out.println(b == b2); // false
        // - Khi ko cùng nội dung thì kiểm tra pool có thì lấy chưa thì tạo xong lấy

        // Tạo đối tượng bằng new String()
        String c = "java";
        String d = new String("java");

        System.out.println(c == d); // false
        System.out.println(c.equals(d)); // true }
        // Khi Sử dụng new . JAva tạo ra một Object mới hoàn toàn. Và ko lưu gì vào
        // trong Pool cả. Cũng ko hề truy cập vào nó
        // Kể cả nội dung chúng giống nhau. Nhưng ko hề được Cache ở đây

        // Khi nối chuỗi thành một String có sẵn trong pool
        String e = "Hello";
        String f = "World";
        String g = "Hello" + "World"; // Biến được tạo ra tại compileTime
        String h = e + f; // Biến tạo ra tại runtime

        System.out.println(g == "HelloWorld"); // true - Được tối ưu tại thời điểm ComplieTime
        System.out.println(h == "HelloWorld"); // false - Trong runTime thì String mới được sinh ra sẽ không truy cập
                                               // vào pool mà tạo mới
    }

    /**
     * Kiểm tra xe String có thể so sánh nội dung với nhau bằng toán tử equals
     */
    public static void stringCanCompareWithEqual() {
        String a = "Hello";
        String b = new String("Hello");
        String c = "World";

        System.out.println(a.equals(b)); // true - Nội dung đều là "Hello"
        System.out.println(a.equals(c)); // false - Nội dung là Hello != World
    }

    /**
     * Demo chứng mình String có các method được cài đặt sẵn tiện ich để thao tác
     * với String
     */
    public static void stringHasMethods() {
        String a = "Hello World";
        System.out.println(a.indexOf('e')); // 1 - Vị trí của kí tự e là 1
        System.out.println(a.length()); // 11 - Lây ra độ dài
    }

    /**
     * String có thể biến đổi nhanh từ String sang array chứa các char[] và ngược
     * lại
     */
    public static void stringCanTransferToArrayAndReverse() {
        String a = "Hello World";
        char[] chars = a.toCharArray();
        String b = String.valueOf(chars);
        System.out.println(a); // Hello World - Khởi tạo bạn đầu
        System.out.println(Arrays.toString(chars)); // [H, e, l, l, o, , W, o, r, l, d] // Biến đổi thành char[]
        System.out.println(b); // Hello World - Biến ngược lại thành String
    }

    /**
     * String có thể được nối với nhau theo dạng literal với toán tử + để nối với
     * nhau
     */
    public static void stringCanConcatWithOperandPlus() {
        String a = "Hello";
        String b = "World";
        String c = a + " " + b;
        System.out.println(c.equals("Hello World")); // true
        // Vậy ta đã tạo ra được một String mới từ việc nối 2 chuỗi bằng toán tử +
    }

    /*
     * Kiểm tra String đã implêmnêtd Comparable chưa
     */
    public static void stringImplementedComparable() {
        String s = "a";
        String s2 = "b";

        System.out.println(s.compareTo(s2)); // -1 : Đã triển khai sẵn
        // String sẽ so sánh theo thứ tự từ điển

        String[] strs = new String[] { "f", "d", "c", "b", "a" };
        Arrays.sort(strs);
        System.out.println(Arrays.toString(strs)); // [a, b, c, d, f]
        // Vậy nó thực sự được sắp xếp lại
    }

}