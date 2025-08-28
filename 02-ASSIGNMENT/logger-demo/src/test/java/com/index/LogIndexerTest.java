package com.index;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

public class LogIndexerTest {
    @Test
    public void testIndex() {
        LogMetaCache cache = new LogMetaCache();
        int trunkSize = 3;
        int nThreads = 5;
        File file = new File("log.txt");
        System.out.println(file.length());
        ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        LogIndexer indexer = new LogIndexer("log.txt", cache, trunkSize, executorService);
        indexer.index();
    }
}
