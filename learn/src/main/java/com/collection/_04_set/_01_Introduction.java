package com.collection._04_set;

import java.util.HashSet;
import java.util.Set;

public class _01_Introduction {
    public static void main(String[] args) {
        // setContainUnique();
        setUseEqualAndHashCode();
    }

    /**
     * Chứng minh set chỉ chứa các dữ liệu độc nhất
     */
    public static void setContainUnique() {
        Set<Integer> set = new HashSet<>();

        set.add(1);
        set.add(2);
        set.add(1);
        System.out.println(set);// [1,2] - Chỉ chứa các phần tử độc nhất

    }

    /**
     * Chứng minh set sử dụng Equal và HashCode để kiểm tra trùng lặp
     * 
     * NX:
     * 
     * - Khi có 2 phần tử trùng lặp. set.add() sẽ ko thêm hay thay thế phần tử cũ
     * - Ta có thể sử dụng contains để kiểm tra một phần tử đã tồn tại trước đó hay
     * chưa
     * - Vậy là Equals() và hashCode() thực sự xác định độ độc nhất của mỗi phần tử
     */
    public static void setUseEqualAndHashCode() {
        Set<Example> set = new HashSet<>();
        Example example = new Example(1, 1);
        Example example2 = new Example(1, 2); // Khác hashCode() == false
        Example example3 = new Example(1, 1); // equals() == hashCode() == true
        Example example4 = new Example(2, 1); // Khác equal() == false
        set.add(example);
        set.add(example2);
        
        System.out.println(set.contains(example3)); // true - Thực sự đã cho rằng example3 == example
        
        set.add(example3);
        set.add(example4);

        System.out.println(set.size()); // 3
        System.out.println(set);
        // [Example [v=1, i=1], Example [v=2, i=1], Example [v=1, i=2]]
        //

    }

    /**
     * 
     */
    public static class Example {
        private int value;
        private int index;

        public Example(int value, int index) {
            this.value = value;
            this.index = index;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * override hashcode theo index
         */
        @Override
        public int hashCode() {
            return index;
        }

        /**
         * Ghi đè lại hành vi equal là theo: value
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Example other = (Example) obj;

            // Khi 2 value = nhau là = nhau
            return other.value == this.value;
        }

        @Override
        public String toString() {
            return "Example [v=" + value + ", i=" + index + "]";
        }

    }

}
