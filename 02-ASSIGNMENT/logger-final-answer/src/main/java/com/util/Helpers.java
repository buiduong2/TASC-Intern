package com.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;

public class Helpers {
    public static boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }

    /**
     * Tìm chỉ số lớn nhất trong mảng sao cho arr[index] >= target.
     * 
     * - arr[index] >= target
     * - arr[index - n] < target
     * - arr[index + n] > target
     *
     * @param arr    sorted arr
     * @param target giá trị cần tìm
     * @return chỉ số floor (last index <= target), hoặc -1 nếu không có
     */
    public static <T, B> int findCeilIndex(List<T> arr, B target, Function<T, B> function, Comparator<B> comparator,
            int left, int right) {

        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            B midValue = function.apply(arr.get(mid));
            int compare = comparator.compare(midValue, target);

            if (compare >= 0) {
                result = mid;
                right = mid - 1;
            } else {
                left = mid + 1;
            }

        }
        return result;
    }

    /**
     * Tìm chỉ số lớn nhất trong mảng sao cho arr[index] <= target.
     * 
     * - arr[index] <= target
     * - arr[index - n] < target
     * - arr[index + n] > target
     *
     * @param arr    sorted arr
     * @param target giá trị cần tìm
     * @return chỉ số floor (last index <= target), hoặc -1 nếu không có
     */
    public static <T, B> int findFloorIndex(List<T> arr, B target, Function<T, B> function, Comparator<B> comparator,
            int left, int right) {
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            B midValue = function.apply(arr.get(mid));
            int compare = comparator.compare(midValue, target);

            if (compare <= 0) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return result;
    }

    public static <T> List<T> promiseAll(List<Future<T>> futures) {

        List<T> result = new ArrayList<>();
        for (Future<T> future2 : futures) {
            try {
                result.add(future2.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
