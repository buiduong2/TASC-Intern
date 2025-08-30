package com;

import com.log.Query;

public class Main {
    public static void main(String[] args) {
        long start = System.nanoTime();
        String resultDir = "logs";
        String regex = "^\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\] \\[([A-Z]+)\\] \\[(\\S+)\\] - (.+)\\s*$";
        String timestampRegex = "yyyy-MM-dd HH:mm:ss";
        int nWorker = 20;
        LogQueryManager queryManager = new LogQueryManager(resultDir, regex, timestampRegex, nWorker);

        String inputName = "demo.txt";
        String outputName = "result";
        int chunkSize = 50000;
        Query query = new Query();
        query.setService("PaymentService");
        query.setLevel("DEBUG");
        // query.setKeyword("expired");
        query.setAfter("2025-08-26 03:58:25");
        query.setBefore("2025-08-31 22:51:42");

        queryManager.searchAndWrite(inputName, outputName, query, chunkSize);
        long end = System.nanoTime();
        System.out.println("Executed in " + (end - start) / 1_000_000 + " ms");

        // 600ms
    }
}