package com.collection._03_queue;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class _01_Introduction {
    public static void main(String[] args) {
        // queueRemainOrder();
        // queueOperation();
    }

    /**
     * Queue duy trì các phần tử được thêm vào theo thứ tự
     * 
     * NX: Queue có thể duy trì các phần tử được thêm vào theo thứ tự thêm. Hoặc
     * theo thứ tự có quy tắc
     */
    public static void queueRemainOrder() {
        // Queue
        Queue<Integer> queue = new LinkedList<>();
        queue.add(1);
        queue.add(2);
        queue.add(3);
        System.out.println(queue);// [1, 2, 3] - Duy trì đúng thứ tự được thêm vào

        // PriorityQueue
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.naturalOrder()); // Max Heap
        maxHeap.add(100);
        maxHeap.add(1);
        maxHeap.add(-1);
        maxHeap.add(3);
        System.out.println(maxHeap); // [-1, 3, 1, 100] - Duy trì các phần tử theo dạng từ thấp -> cao
    }

    /**
     * Các toán tử khi làm việc với Queue
     * 
     * - Chủ yếu làm việc theo mô hình FIFO (first in First Out)
     * 
     * - Chỉ cung các các method làm việc ở peek(). ko hỗ trợ truy cập ở giữa random
     * giống list
     */
    public static void queueOperation() {
        Queue<Integer> queue = new LinkedList<>();

        // Thêm một phần tử
        queue.add(1);
        queue.add(2);
        queue.add(3);
        System.out.println(queue.size()); // 3 - Có 3 phần tử
        System.out.println(queue); // [1, 2, 3] -> danh sách các phần tử đã thêm

        // Xóa phần tử ở đầu
        int last = queue.poll();
        System.out.println(last); // 1; - Lấy ra phần tử và xóa phần tử ở cuối
        System.out.println(queue); // [2,3] - Đã xóa phần tử

        // Xem phần tử ở đầu (đỉnh / tương tác tiếp theo)
        System.out.println(queue.peek());// 2

    }

}
