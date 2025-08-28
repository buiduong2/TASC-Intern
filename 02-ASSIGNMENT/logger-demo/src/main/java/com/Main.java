package com;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.index.LogIndexer;
import com.index.LogMetaCache;
import com.query.LogQuery;
import com.query.LogQueryService;
import com.query.QueryCondition;
import com.reader.LogFileReader;
import com.reader.ResultMerger;

public class Main {
    public static void main(String[] args) {

        int trunkSize = 1;
        int nThreads = 30;
        String fileName = "log.txt";

        LogMetaCache cache = new LogMetaCache();
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        LogIndexer logIndexer = new LogIndexer(fileName, cache, trunkSize, executorService);
        ResultMerger merger = new ResultMerger();
        LogFileReader logFileReader = new LogFileReader(executorService, fileName, merger);
        LogQuery query = new LogQuery(cache);
        LogQueryService service = new LogQueryService(query, trunkSize, fileName, logFileReader);

        // Initital
        System.out.println("BEGIN index");
        logIndexer.index();
        System.out.println("AFTER INDEX");

        // Prepare
        QueryCondition condition = new QueryCondition();
        condition.setAfter("2025-08-26 03:58:25");
        condition.setBefore("2025-08-26 03:58:26");
        condition.setLevel("DEBUG");
        condition.setService("AuthService");
        condition.setKeyword("expired");

        // Execute
        service.execute(condition);

        executorService.shutdown();

    }
}