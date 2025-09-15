package com.backend.utils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    public static String prettyJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json;
        }
    }

    public static <T> T getRandomElement(List<T> list) {
        if (list == null || list.isEmpty()) {
            return null; // hoặc throw exception tùy bạn
        }
        Random random = new Random();
        int index = random.nextInt(list.size()); // tạo số ngẫu nhiên từ 0..size-1
        return list.get(index);
    }

    public static boolean afterOrEqual(LocalDateTime a, LocalDateTime b) {
        if (a == null || b == null) {
            return false; // hoặc throw IllegalArgumentException tùy bạn muốn
        }
        return a.isAfter(b) || a.isEqual(b);
    }
}
