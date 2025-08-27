package com.collection._03_queue;

import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

public class _02_Implementtions {

    public static void main(String[] args) {

        // deQueueIntroduction();
        // priorityQueueIntroduction();
        // nonThreadSafeQueue();
        // concurrentQueueIntroduction();
        // blockingQueueIntroduction();
    }

    /**
     * Dequeue: hỗ trợ thêm và xóa phần tử ở cả 2 đầu
     * 
     * - Có đầy đủ các method của Queue nhưng có thêm các method rieng biệt phục vụ
     * ở cuối hagnf đợi
     * 
     * - Có thể làm việc như một Stack
     */
    public static void deQueueIntroduction() {
        Deque<Integer> dequeue = new LinkedList<>();

        // Các method của Queue
        dequeue.add(1);
        dequeue.add(2);
        System.out.println(dequeue.size()); // 2
        System.out.println(dequeue);// [1,2]

        // Method thao tác ở đầu dãy vừa thêm vào
        dequeue.addFirst(0);
        System.out.println(dequeue); // [0, 1, 2] - Chèn phần tử vào đầu queue
        int last = dequeue.pollLast(); // Xóa phần tử ở cuối queue
        System.out.println(last); // 2
    }

    /**
     * Làm việc với priorityQueue.
     * 
     * NX:
     * - Là một dạng hàng đợi ưu tiên.
     * - PHần tử nào có độ ưu tiên cao hơn sẽ được cho ra trước ko quan tâm đến thứ
     * tự thêm vào
     */
    public static void priorityQueueIntroduction() {
        Queue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());
        maxHeap.add(1);
        maxHeap.add(5);
        maxHeap.add(-1);
        System.out.println(maxHeap); // [5, 1, -1] -> Dảy giảm dần

        Queue<Integer> minHeap = new PriorityQueue<>(Comparator.naturalOrder());
        minHeap.add(1);
        minHeap.add(5);
        minHeap.add(-1);
        System.out.println(minHeap); // [-1, 5, 1] -> Dãy tăng dần

        // Khi lấy các phần tử
        System.out.println(maxHeap.poll()); // 5 -> Lấy ra phần tử lớn nhất rồi đén các phần tử nhỏ hơn
        System.out.println(minHeap.poll()); // -1 -> Lấy ra phần tử nhỏ nhất
    }

    /**
     * Kiểm tra tính ThreadSafe của queue
     * - Ta sẽ tạo ra một Queue 100 phần tử
     * - 2 thread tiến hành poll đồng thời
     * - Ta kiểm tra xem có phải sẽ có lúc poll ra phần tử trùng lặp không
     * 
     * KQ: đôi khi bị vòng lặp vô hạn
     * 
     * - Đôi khi set sẽ nhận được 2 phần tử giống nhau
     * 
     * - Vậy thread ko hề safe rồi
     */
    public static void nonThreadSafeQueue() {
        Queue<Integer> queue = new LinkedList<>();
        IntStream.range(0, 1000).forEach(queue::add);
        // Thêm vào Queue 100 phần tử từ 0 -> 100
        Set<Integer> set = new HashSet<>();

        Runnable task = () -> {
            while (!queue.isEmpty()) {
                Integer first = queue.poll();
                if (first != null) {
                    if (set.contains(first)) {
                        System.out.println("Race condition");
                    }
                    set.add(first);
                } else {
                    break;
                }

            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();
        try {

            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Chương trình kết thúc");
    }

    /**
     * Làm việc với các Class ConcurrentQueue
     * 
     * NX:
     * - Tránh race-condition ở mức cấu trúc nhưng vẫn có tình huống đọc giá trị cũ
     * (kiểu lưu giá trị vào trong thread nhưng đó ko phải là mới nhất)
     * 
     */
    public static void concurrentQueueIntroduction() {
        Queue<Integer> queue = new ConcurrentLinkedDeque<>();
        IntStream.range(0, 1000).forEach(queue::add);
        // Thêm vào Queue 100 phần tử từ 0 -> 100
        Set<Integer> set = new HashSet<>();

        Runnable task = () -> {
            while (!queue.isEmpty()) {
                Integer first = queue.poll();
                if (first != null) {
                    if (set.contains(first)) {
                        System.out.println("Race condition");
                    }
                    set.add(first);
                } else {
                    break;
                }

            }
        };
        Thread thread1 = new Thread(task);
        Thread thread2 = new Thread(task);

        thread1.start();
        thread2.start();
        try {

            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Chương trình kết thúc");

    }

    /**
     * BLockingQueue
     * 
     * - MỘt nhóm các Queue có chức năng theo kiến thức producer - consumer . Khi mà
     * ta thêm hoặc bớt nó sẽ đợi cho đến khi đạt đủ điều kiện mới thực hiện Còn ko
     * sẽ block thread và đời
     * 
     * KQ:
     * - Chỉ khi có phần tử thì take() mới trả về kết quả. Nếu ko nó sẽ block
     * - Việc lấy ra tất cả các Queue đều ko thiếu một phần tử nào
     * 
     */
    public static void blockingQueueIntroduction() {
        // Giả lập nhà cung cấp cứ 1s mới tiến hành cung cấp 1 sản phẩn
        BlockingDeque<Integer> queue = new LinkedBlockingDeque<>();

        Thread cook = new Thread(() -> {
            int times = 10;
            while (times > 0) {
                queue.add(times);
                System.out.println("add times: " + times);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                times--;
            }
        });

        Thread waiter = new Thread(() -> {
            int times = 10;
            while (times > 0) {
                times--;
                try {
                    int order = queue.take();
                    System.out.println("Take: " + order);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        cook.start();
        waiter.start();
        try {
            waiter.join();
            cook.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
