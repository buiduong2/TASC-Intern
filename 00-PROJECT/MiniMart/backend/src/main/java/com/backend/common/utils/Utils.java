package com.backend.common.utils;

public class Utils {
    @SafeVarargs
    public static <T> T coalesce(T... values) {
        for (T val : values) {
            if (val != null)
                return val;
        }
        return null;
    }
}
