package com.collection._04_set;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class _02_Implementations {

    public static void main(String[] args) {
        // hashSet();
        // linkedHashSet();
        treeSet();
    }

    /**
     * Implementation của Set có HashSet
     * 
     * - Sử dụng hashtable
     * - Không duy trì thứ tự
     */
    public static void hashSet() {
        HashSet<Integer> set = new HashSet<>();

        set.add(3);
        set.add(1);
        set.add(2);
        set.add(-100);

        System.out.println(set); // [1, 2, 3, -100]
        // Thứ tự thêm vào không được đảm bảo

    }

    /**
     * Implementation của Set có HashSet
     * 
     * - Sử dụng hashtable + linkedList
     * - Duy trì thứ tự thêm vào
     */
    public static void linkedHashSet() {
        LinkedHashSet<Integer> set = new LinkedHashSet<>();

        set.add(3);
        set.add(1);
        set.add(2);
        set.add(-100);

        System.out.println(set); // [3, 1, 2, -100]
        // Đã duy trì thứ tự thêm vào

        // Lấy ra phần tử đầu tiên
        System.out.println(set.getFirst());

        // Lấy ra phần tử cuối cùng
        System.out.println(set.getFirst());

    }

    /**
     * Implementation của Set có TreeSet
     * 
     * - Sử dụng black-red Tree
     * 
     * - Duy trì các phàn tử thêm vào theo một Comparator
     */
    public static void treeSet() {
        TreeSet<Integer> set = new TreeSet<>();
        set.add(5);
        set.add(3);
        set.add(1);
        set.add(100);
        set.add(-1);

        System.out.println(set);
        // [-1, 1, 3, 5, 100] - Thứ tự mặc định tăng dần

        // Cung cáp thêm các method tiện ích khác

        // Lấy ra phần tử Root (hiện tại là nhỏ nhất)
        int first = set.first();
        System.out.println(first); // -1

        // Lấy ra phần tử lớn nhất gần với input nhất. Nhưng nhỏ hơn input
        // (theo Comparator)
        int floor = set.floor(4);
        System.out.println(floor); // 3

        // Lấy ra phần tử lớn nhất gần input nhất
        int ceilling = set.ceiling(4);
        System.out.println(ceilling); // 5

        // Lấy tất cả phần tử sao cho comparator < 0
        Set<Integer> lessThan5 = set.headSet(5);
        System.out.println(lessThan5); // [-1, 1, 3]

    }
}
