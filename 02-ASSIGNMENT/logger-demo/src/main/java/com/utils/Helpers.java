package com.utils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class Helpers {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static <T> List<List<T>> trunk(List<T> list, int trunkSize) {
        if (list == null || list.isEmpty() || trunkSize <= 0) {
            return Collections.emptyList();
        }

        List<List<T>> result = new ArrayList<>();
        int size = list.size();

        for (int i = 0; i < size; i += trunkSize) {
            int end = Math.min(i + trunkSize, size);
            result.add(list.subList(i, end));
        }

        return result;
    }

    public static List<List<Long>> trunkIndexes(long length, long trunkSize) {
        if (length <= 0 || trunkSize <= 0) {
            return Collections.emptyList();
        }

        List<List<Long>> ranges = new ArrayList<>();
        for (long start = 0; start < length; start += trunkSize) {
            long end = Math.min(start + trunkSize - 1, length - 1);
            ranges.add(new ArrayList<>(List.of(start, end)));
        }

        return ranges;
    }

    public static List<List<Long>> normalizeTrunks(List<List<Long>> trunkFiles) {
        List<List<Long>> offsets = new ArrayList<>();
        Long last = null;

        for (int i = 0; i < trunkFiles.size(); i++) {
            Long start = trunkFiles.get(i).get(0);
            if (!start.equals(last)) {
                offsets.add(new ArrayList<>());
                offsets.get(offsets.size() - 1).add(start);
            }
            last = start;
        }

        for (int i = 0; i < offsets.size() - 1; i++) {
            offsets.get(i).add(offsets.get(i + 1).get(0));
        }

        offsets.getLast().add(trunkFiles.get(trunkFiles.size() - 1).get(1));
 

        return offsets;
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

    public static long parseTimestampLong(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            throw new IllegalArgumentException("Timestamp is null or empty");
        }

        LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);

        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static void readLine(long startOffset, long endOffset, RandomAccessFile raf, Consumer<String> consumer)
            throws IOException {

        raf.seek(startOffset);

        while (raf.getFilePointer() < endOffset) {
            String line = raf.readLine();
            if (line == null) {
                break;
            }
            consumer.accept(line);
        }
    }

}
