package com.collection._01_array;

import java.util.Arrays;

public class _01_Introduction {
    public static void main(String[] args) {
        // declareArray();
        // indexOfArray();
        // arrayDimension();
        // defaultValueOfElement();
        utilitiesMethod();
    }

    /**
     * Các cách khai báo Array
     * 
     * NX:
     * 
     * - Mảng có thể khai báo theo 2 cách. Khai báo trước độ dài của mảng
     * - Hoặc khai báo các phần tử của mảng luôn
     */
    public static void declareArray() {
        // Khởi tạo Array với số lượng phần tử của mảng

        // Khai báo 1 mảng có 6 phần tử
        int[] arr1 = new int[6];
        System.out.println(arr1.length); // 6 - Có 6 phần tử

        // Khai báo Literal
        int[] arr2 = { 1, 2, 3, 4, 5, 6 };
        System.out.println(Arrays.toString(arr2)); // [1, 2, 3, 4, 5, 6]
    }

    /**
     * Làm việc với index của array
     * 
     * NX:
     * - Các chỉ mục có thể truy cập từ số 0 . cho đến length-1
     * - Khi truy cập vào chỉ mục vượt quá giới hạn sẽ throw ra exception
     * ArrayIndexOutOfBoundsException
     * - Với mảng nhiều chiều. Khi ta tủy cập vào chỉ mục của mảng trả về 1 mảng. Ta
     * có thể tiếp tục sử dụng toán tử chỉ mục tiếu arr[index1][index2]
     * 
     */
    public static void indexOfArray() {
        // Truy cập phần tử hợp lệ
        int[] nums = { 1, 2, 3, 4, 5 };
        // Mảng có 5 phần tử. Từ index 0 -> index = 4

        System.out.println(nums[0]); // 1
        System.out.println(nums[4]); // 5

        try {
            System.out.println(nums[5]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Exception xảy ra vì truy cập vào ngoài phạm vị của mảng ");
        }
        // Exception x?y ra vì truy c?p vào ngoài ph?m v? c?a m?ng

        // Truy cập vào phần tử hợp lệ của mảng 2 chiều
        int[][] matrix = { { 1, 2, 3 }, { 4, 5, 6 } };
        System.out.println(matrix.length);// 2 - Mảng bên ngoài sẽ chứa 2 phần tử là các array khác
        System.out.println(matrix[0].length); // 3 - Các array phần tử bên trong sẽ chứa 3 phần tử khác là số nguyên

        System.out.println(matrix[0][0]); // 1
        System.out.println(matrix[0][2]);// 3
    }

    /**
     * Mảng có thể có nhiều chiều
     * 
     * NX : Ta có thể tạo ra mảng bao nhiêu chiều tùy thích
     * - Chỉ cần tuân theo quy luật các phần tử trong một mảng có cùng kiểu dữ liệu
     * - Nếu phần tử là array thì các phần tử còn lại là array.
     * - Mảng đa chiều thực chất là một array chứa các array khác
     */
    public static void arrayDimension() {
        // Mảng 1 chiều
        int[] oneDimension = { 1, 2, 3, 4, 6 };

        for (int i = 0; i < oneDimension.length; i++) {
            System.out.print(oneDimension[i] + " ");
        }
        System.out.println();

        // Mảng 2 chiều
        int[][] twoDimension = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };

        System.out.println("Mảng 2 chiều");
        for (int i = 0; i < twoDimension.length; i++) {
            for (int j = 0; j < twoDimension[i].length; j++) {
                System.out.print(twoDimension[i][j] + " ");
            }
            System.out.println();
        }

        /**
         * KQ:
         * 1 2 3
         * 4 5 6
         * 7 8 9
         */

    }

    /**
     * Kiểm tra các phần tử của Array khi chúng chưa được khai báo
     * 
     * - NX: Vậy là các phần tử của mảng khi chưa khai báo gán giá trị thì nó tuân
     * thủ như một field của một instance trong Object
     */
    public static void defaultValueOfElement() {
        // Primitive
        int[] ints = new int[6];
        double[] doubles = new double[6];
        boolean[] bools = new boolean[6];
        System.out.println(Arrays.toString(ints)); // [0, 0, 0, 0, 0, 0]
        System.out.println(Arrays.toString(doubles)); // [0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
        System.out.println(Arrays.toString(bools)); // [false, false, false, false, false, false]

        // Object
        String[] strs = new String[6];
        System.out.println(Arrays.toString(strs));// [null, null, null, null, null, null]
    }

    /**
     * Các phương thức khi làm việc với mảng
     * 
     * - NX:
     * - Mảng không có nhiều phương thức
     * - Chúng ta có sẵn một thuộc tính là length . Để truy cập xem độ dài của mảng
     * - Ngoài ra ta có thể làm việc với Arrays để có sẵn các phương thức tiện ích
     * khi làm việc với mảng
     */
    public static void utilitiesMethod() {
        int[] arr = { 3, 2, 1, 0 };
        System.out.println(arr.length); // 4 -
        // length là một thuộc tính giúp truy cập vào độ dài của mảng

        // Arrays
        Arrays.sort(arr); // Sắp xếp các phần tử của mảng
        System.out.println(Arrays.toString(arr)); // 0, 1, 2, 3] - Biến đổi mảng thành String mà có thể dọc
        int[] arr2 = { 0, 1, 2, 3 };
        System.out.println(arr.equals(arr2)); // false - equals của arr ko so sánh nội dung
        System.out.println(Arrays.equals(arr, arr2)); // true - Phương thức so sánh nội dung của mảng
    }

}
