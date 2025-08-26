package com.thread._01_synchronize_asynchronous;

public class _01_Introduction {
    public static void main(String[] args) {
        // synchronizeProcess();
        asynchronousProcess();
    }

    /*
     * Ta tiến hành mô phỏng chương trình làm việc đồng bộ.
     * 
     * NX:
     * 
     * - Mặc dù 2 công việc ko sử dụng tài nguyên hay liên quan đến nhau'
     * - Nhưng Công việc đầu tiên xong thì công với thứ 2 mới bắt đầu
     * - Mất tổng có thể lên đến 2000 + 100 = 2100ms
     * 
     * - Code đọc từ trên xuống dưới
     */
    public static void synchronizeProcess() {

        slowTask();
        quickTask();
        // === SlowTask begin ===
        // === SlowTask End ===
        // === QuickTask begin ===
        // === QuickTask End ===
    }

    /**
     * MÔ phỏng chương trình bất đồng bộ
     * 
     * - Code đọc bất đồng bộ giữa các Runnable khác nhau
     * 
     * - Các Thread sẽ làm việc song song với nhau
     */
    public static void asynchronousProcess() {
        Thread slowThread = new Thread(() -> slowTask());
        Thread quickThread = new Thread(() -> quickTask());

        slowThread.start();// Slow bắt đầu công việc trước
        quickThread.start(); // Quick bắt đầu công việc sau

        // === QuickTask begin ===
        // === SlowTask begin ===
        // === QuickTask End ===
        // === SlowTask End ===
        /*
         * Vậy là chương trình sẽ chạy bất đồng bộ không theo code
         */
    }

    /**
     * Mô phỏng 1 công việc nhanh
     */
    public static void quickTask() {
        System.out.println(" === QuickTask begin ===");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("=== QuickTask End ===");
    }

    /**
     * Mô phỏng 1 Công việc lâu
     */
    public static void slowTask() {
        System.out.println(" === SlowTask begin ===");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(" === SlowTask End ===");
    }
}
