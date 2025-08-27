package com.collection._05_map;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class _02_SafeThread {

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            // hashmapNotSafe();
            // linkedHashMapNotSafe();
            concurrentMapSafe();
            System.out.println("Lần thứ: " + i + " Kết thúc");
        }
    }

    public static void addValueToMap(Map<Integer, Integer> map) {
        for (int i = 1; i < 8000; i++) {
            map.put(i, i);
        }
    }

    public static void clearMap(Map<Integer, Integer> map) {
        for (int i = 1; i < 8000; i++) {
            map.remove(i);
        }
    }

    public static void checkMapSafeWithThread(Map<Integer, Integer> map) {
        addValueToMap(map);
        Runnable r = () -> clearMap(map);

        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!map.isEmpty() || map.size() != 0) {
            System.out.println("Dữ liệu bị sai");
            System.out.println("Kích thước mong muốn: 0 , Kích thước thật: " + map.size());

        }
    }

    public static void linkedHashMapNotSafe() {
        checkMapSafeWithThread(new LinkedHashMap<>());
    }

    public static void concurrentMapSafe() {
        checkMapSafeWithThread(new ConcurrentHashMap<>());
    }

    public static void hashmapNotSafe() {
        checkMapSafeWithThread(new HashMap<>());
    }

}
