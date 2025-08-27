package com.collection._05_map;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

public class _03_Implementations {
    public static void main(String[] args) {
        // hashMap();
        // linkedHashMap();
        // treeMap();
        // weakMap();
        identityHashMap();
    }

    /**
     * - HashMap Sử dụng hashTable.
     * - Không duy trì thứ tự thêm vòa
     */
    public static void hashMap() {
        HashMap<Character, Integer> map = new HashMap<>();
        map.put('B', 2);
        map.put('C', 3);
        map.put('A', 1);
        map.put('D', 4);

        Set<Map.Entry<Character, Integer>> set = map.entrySet();
        System.out.println(map); // {A=1, B=2, C=3, D=4}
        System.out.println(set); // [A=1, B=2, C=3, D=4]
    }

    /**
     * LinkedHashMap duy trì thứ tự như lúc thêm vào
     */
    public static void linkedHashMap() {
        LinkedHashMap<Character, Integer> map = new LinkedHashMap<>();
        map.put('B', 2);
        map.put('C', 3);
        map.put('A', 1);
        map.put('D', 4);

        System.out.println(map.firstEntry()); // B=2
        System.out.println(map.lastEntry()); // D=4

        System.out.println(map.entrySet()); // [B=2, C=3, A=1, D=4]

    }

    /**
     * TreeMap duy trì thứ tự các Key theo Comparator
     */

    static void treeMap() {
        // Map với Comparator<Character> tự nhiên
        TreeMap<Character, Integer> map = new TreeMap<>();
        map.put('B', 2);
        map.put('C', 3);
        map.put('A', 1);
        map.put('D', 4);

        System.out.println(map.firstEntry()); // A=1
        System.out.println(map.lastEntry()); // D=4

        System.out.println(map.entrySet()); // [A=1, B=2, C=3, D=4] - Duy trì thứ tự

        // Có các method giống của treeSet nhưng làm viecj với entry
        // Lấy ra phần tử lớn nhất gần input nhất theo key
        Map.Entry<Character, Integer> ceilling = map.ceilingEntry('B');
        System.out.println(ceilling); // B

        // Lấy tất cả phần tử sao cho comparator < 0 theo key
        Map<Character, Integer> lessThan5 = map.headMap('C');
        System.out.println(lessThan5); // {A=1, B=2}
    }

    public static class MyKey {
        private char c;

        public MyKey(char c) {
            this.c = c;
        }

        @Override
        public String toString() {
            return "MyKey [c=" + c + "]";
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + c;
            return result;
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
            if (c != other.c)
                return false;
            return true;
        }

    }

    /**
     * Tự động xóa key-value khi mà key có weak reference (ko được tham chiếu =
     * varaible nữa)
     */
    static void weakMap() {
        WeakHashMap<MyKey, Integer> weakMap = new WeakHashMap<>();
        Map<Character, MyKey> strongMap = new HashMap<>();
        MyKey c1 = new MyKey('B');
        MyKey c2 = new MyKey('C');
        MyKey c3 = new MyKey('A');
        weakMap.put(c1, 2);
        weakMap.put(c2, 3);
        weakMap.put(c3, 1);

        strongMap.put('D', new MyKey('D'));
        weakMap.put(strongMap.get('D'), 4);

        System.out.println("Trước khi Rọn rác");
        System.out.println(weakMap); // {MyKey [c=B]=2, MyKey [c=A]=1, MyKey [c=D]=4, MyKey [c=C]=3}

        try {
            // Đảm bảo trình dọn rác rọn luôn
            // -- Demo. Trong thực tế ko đảm bảo nó chạy lúc nào
            System.gc();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Sau khi tự động rọn rác lần 1");
        System.out.println(weakMap); // {MyKey [c=B]=2, MyKey [c=A]=1, MyKey [c=D]=4, MyKey [c=C]=3}

        System.out.println("Tiến hành tạo weak Key ");
        strongMap.remove('D');
        System.out.println(weakMap); // {MyKey [c=B]=2, MyKey [c=A]=1, MyKey [c=D]=4, MyKey [c=C]=3}

        try {
            // Đảm bảo trình dọn rác rọn luôn
            // -- Demo. Trong thực tế ko đảm bảo nó chạy lúc nào
            System.gc();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("rọn Rác");
        System.out.println(weakMap); // {MyKey [c=B]=2, MyKey [c=A]=1, MyKey [c=C]=3}
        // Đã mất MyKey [c=D]=4
        System.out.println(weakMap.containsKey(new MyKey('D'))); // false
    }

    public static class MyKey2 {
        private char c;

        public MyKey2(char c) {
            this.c = c;
        }

        @Override
        public String toString() {
            return "MyKey2 [c=" + c + "]";
        }

        // Hash Code và equal luôn giống nhau
        @Override
        public int hashCode() {
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            return true;
        }

    }

    /**
     * identityHashMap - không sử dụng equal() mà sử dụng reference ==
     * 
     * - Viecj so sánh sử dụng tham chiếu sẽ tiết kiệm chi phí hơn
     */
    static void identityHashMap() {
        MyKey2 k = new MyKey2('A');
        MyKey2 k2 = new MyKey2('B');
        MyKey2 k3 = new MyKey2('C');

        IdentityHashMap<MyKey2, Character> map = new IdentityHashMap<>();
        Map<MyKey2, Character> normalMap = new HashMap<>();
        map.put(k, 'A');
        map.put(k2, 'B');
        map.put(k3, 'B');

        normalMap.put(k, 'A');
        normalMap.put(k2, 'B');
        normalMap.put(k3, 'B');

        System.out.println(map);
        // {MyKey2 [c=C]=B, MyKey2 [c=B]=B, MyKey2 [c=A]=A}

        // Các key đều được thêm vào khác với Hashmap
        System.out.println(normalMap); // {MyKey2 [c=A]=B} - Phần tử cuối cùng được thêm

        // Kiểm tra sự tồn tại
        System.out.println(map.containsKey(k)); // true
        System.out.println(map.containsKey(new MyKey2('A'))); // false - khác tham chiếu
    }
}
