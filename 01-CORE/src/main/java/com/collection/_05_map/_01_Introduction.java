package com.collection._05_map;

import java.util.HashMap;
import java.util.Map;

public class _01_Introduction {
    public static void main(String[] args) {
        // keyMapToValue();
        // mapRemaindUniqueKey();
        // mapWorkWithEqualAndHashCode();
        mapOperation();
    }

    /**
     * Map là một tập hợp gồm nhiều key ánh xạ đến Value
     * 
     * 
     */
    public static void keyMapToValue() {
        Map<Character, Integer> map = new HashMap<>();

        map.put('A', 1);
        map.put('B', 2);
        map.put('C', 3);

        System.out.println(map);
        // {A=65, B=66, C=67}

        System.out.println(map.get('A')); // 1
        System.out.println(map.get('B')); // 2
    }

    /**
     * Map Duy trì các key luôn là độc nhất
     * 
     * NX:
     * 
     * - Khi ta put trùng một key cũ thì value sẽ được thay thế
     */
    public static void mapRemaindUniqueKey() {
        Map<Character, Integer> map = new HashMap<>();

        // Thêm 1 key và 2 value khác nhau
        map.put('A', 1);
        map.put('A', 2);

        System.out.println(map.get('A')); // 2 Giá trị cũ đã được thay thế
    }

    /**
     * Map làm việc với equal và hashCode
     */
    public static void mapWorkWithEqualAndHashCode() {
        MyKey key1 = new MyKey(1);// hashCode == 1
        MyKey key2 = new MyKey(2);// hashCode == 2

        Map<MyKey, Integer> map = new HashMap<>();
        // Thêm key1
        map.put(key1, 1);

        // Kiểm tra sự trùng lặp key1 so với key2
        System.out.println(map.containsKey(key2)); // false - ko có key nào hợp lệ
        map.put(key2, 2);
        System.out.println(map.size()); // 2
        System.out.println(map); // {MyKey [value=1]=1, MyKey [value=2]=2}

        // Kiểm tra sự trùng lặp key1 so với 1 key có value tương đương
        MyKey likeKey1 = new MyKey(1); // hashCode == 1
        System.out.println(map.containsKey(likeKey1)); // true
        map.put(likeKey1, 3);
        System.out.println(map.size()); // 2 - Kích thước ko thay đổi
    }

    /**
     * Các hoạt động cơ bản với map
     */
    public static void mapOperation() {
        Map<Character, Integer> map = new HashMap<>();
        // Thêm cặp phần tử
        map.put('A', 1);
        map.put('B', 2);
        map.put('C', 3);
        System.out.println(map.size());// 3
        System.out.println(map); // {MyKey [value=1]=1, MyKey [value=2]=2}

        // Lấy ra một phàn tử theo key
        int num = map.get('A'); // 1
        System.out.println(num);

        // Xóa một phần tử theo key
        map.remove('A');
        System.out.println(map.get('A')); // null
        System.out.println(map.size()); // 2

        map.put('A', 1);

        // Kiểm tra nhanh sự tồn tại theo key
        boolean isAExists = map.containsKey('A');
        boolean isDExists = map.containsKey('D');
        System.out.println(isAExists); // true
        System.out.println(isDExists); // false

    }

    /**
     * Tạo ra một Key theo dạng hashCode = value
     */
    public static class MyKey {
        private int value;

        public MyKey(int value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            MyKey other = (MyKey) obj;
            if (value != other.value)
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "MyKey [value=" + value + "]";
        }

    }
}
