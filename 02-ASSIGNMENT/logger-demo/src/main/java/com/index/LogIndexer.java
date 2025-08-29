package com.index;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.model.LogMeta;
import com.utils.Helpers;

/**
 * đọc file log theo thread, tìm offset của từng dòng, tạo ra LogMeta.
 * 
 * - Cập nhật vào cache.
 */
public class LogIndexer {

    private final String fileName;

    private final LogMetaCache cache;

    private final int trunkSize;

    private final ExecutorService executorService;

    public LogIndexer(String fileName, LogMetaCache cache, int trunkSize, ExecutorService executorService) {
        this.fileName = fileName;
        this.cache = cache;
        this.trunkSize = trunkSize;
        this.executorService = executorService;
    }

    public List<List<Long>> getOffsets() {
        System.out.println("BEGIN OFFSET");
        File file = new File(fileName);
        List<List<Long>> trunkFiles = trunkFile(file.length());
        int trunkSize = trunkFiles.size();
        List<Future<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < trunkFiles.size(); i++) {
            List<Long> offsets = trunkFiles.get(i);
            final int position = i == 0 ? -1 : i == trunkSize - 1 ? 1 : 0;

            Future<Integer> future = executorService.submit(() -> {
                try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
                    shiftOffset(offsets, position, raf);

                } catch (IOException e) {
                    e.printStackTrace();

                }
                return 0;
            });
            futures.add(future);

        }

        Helpers.promiseAll(futures);
        System.out.println("AFTER OFFSET");

        return Helpers.normalizeTrunks(trunkFiles);
    }

    public void index() {
        List<List<Long>> pageTrunks = getOffsets();

        System.out.println("BEGIN INDEX");
        List<Future<Integer>> futures = new ArrayList<>();
        List<List<LogMeta>> logMetas = new ArrayList<>();

        for (int i = 0; i < pageTrunks.size(); i++) {
            List<Long> offsets = pageTrunks.get(i);
            final List<LogMeta> list = new ArrayList<>();
            logMetas.add(list);

            Future<Integer> future = executorService.submit(() -> {
                Long startOffset = offsets.get(0);
                long endOffset = offsets.get(1);
                try (RandomAccessFile raf = new RandomAccessFile(fileName, "r")) {
                    raf.seek(startOffset);

                    while (raf.getFilePointer() < endOffset) {
                        String line = raf.readLine();
                        if (line == null) {
                            break;
                        }
                        long start = raf.getFilePointer()
                                - (line.getBytes().length + System.lineSeparator().getBytes().length);
                        long end = raf.getFilePointer();

                        LogMeta logMeta = new LogMeta();
                        logMeta.setStartOffset(start);
                        logMeta.setEndOffset(end);
                        list.add(logMeta);
                        cache.addToCache(logMeta, line);

                    }

                } catch (IOException e) {
                    e.printStackTrace();

                }
                return 0;
            });
            futures.add(future);

        }

        Helpers.promiseAll(futures);

        List<LogMeta> result = logMetas.stream().flatMap(List::stream).toList();
        cache.setLogMetas(result);
        System.out.println("AFTER INDEX");
    }

    private List<List<Long>> trunkFile(long fileLength) {
        return Helpers.trunkIndexes(fileLength, trunkSize);
    }

    /**
     * Dịch chuyển các offset sao cho không cắt giữa dòng
     */
    private void shiftOffset(List<Long> offsets, int position, RandomAccessFile raf) throws IOException {
        boolean isLastTrunk = position == 1;
        boolean isFirsTrunk = position == -1;
        boolean isMiddleTrunk = !isLastTrunk && !isFirsTrunk;
        long endOffset = offsets.get(1);
        long startOffset = offsets.get(0);

        if (isMiddleTrunk || isLastTrunk) {
            raf.seek(startOffset);
            raf.readLine();
            startOffset = raf.getFilePointer();
        }

        if (isFirsTrunk || isMiddleTrunk) {
            raf.seek(endOffset);
            raf.readLine();
            endOffset = raf.getFilePointer();
        }

        offsets.set(0, startOffset);
        offsets.set(1, endOffset);
    }

}
