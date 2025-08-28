package com.reader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Predicate;

import com.model.LogMeta;
import com.utils.Helpers;

/**
 * Nhận danh sách offset cần đọc
 * 
 * Tạo nhiều thread để mở file, đọc chunk theo offset, trả về LogEntry.
 */
public class LogFileReader {

    private final ExecutorService executorService;

    private final String fileName;

    private final ResultMerger merger;

    public LogFileReader(ExecutorService executorService, String fileName, ResultMerger merger) {
        this.executorService = executorService;
        this.fileName = fileName;
        this.merger = merger;
    }

    public void readAndWriteMatchTrunk(List<List<LogMeta>> trunks, Predicate<String> linePredicate,
            String outputFileName) {
        List<Future<String>> futures = new ArrayList<>();

        for (int i = 0; i < trunks.size(); i++) {
            final List<LogMeta> logMetas = trunks.get(i);
            final int suffix = i;

            Future<String> future = executorService.submit(() -> {
                String trunkFileName = outputFileName + "_" + suffix;
                readAndWriteMatch(logMetas, linePredicate, trunkFileName);
                return trunkFileName;
            });
            futures.add(future);
        }

        List<String> fileNames = Helpers.promiseAll(futures);

        merger.mergeFiles(fileNames, outputFileName);
    }

    private void readAndWriteMatch(List<LogMeta> logMetas, Predicate<String> linePredicate, String outputFileName) {
        if (logMetas == null || logMetas.isEmpty())
            return;

        try (RandomAccessFile raf = new RandomAccessFile(fileName, "r");
                BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFileName))) {

            for (LogMeta meta : logMetas) {
                raf.seek(meta.getStartOffset());

                while (raf.getFilePointer() < meta.getEndOffset()) {
                    String line = raf.readLine();
                    if (line == null) {
                        break;
                    }

                    if (linePredicate.test(line)) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
