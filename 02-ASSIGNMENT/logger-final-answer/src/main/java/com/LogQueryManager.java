package com;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.stream.Stream;

import com.io.FileReaderService;
import com.io.FileWriterService;
import com.log.Chunk;
import com.log.LogProcessor;
import com.log.Query;
import com.util.Helpers;
import com.util.LogParsers;

/*
 * trái tim của Chương trình
 * 
 * - Nhận vòa một Queryu
 * 
 * - Đọc file theo trunk (Đọc tuần tự -> Pushs string vào Queue) -> xử tiền xử lý chunk (Đa luồng từ dây) -> xử lý chunk line by line -> ghi trunk -> merge trunnk_file
 */
public class LogQueryManager {

    private final FileReaderService fileReaderService;
    private final LogProcessor processor;
    private final FileWriterService writerService;
    private final ExecutorService executorService;
    private final int nWorker;
    private final String resultDir;

    public LogQueryManager(String resultDir, String logRegex, String timestampRegex, int nWorker) {
        LogParsers parsers = new LogParsers(logRegex, timestampRegex);
        this.processor = new LogProcessor(parsers);

        this.writerService = new FileWriterService(resultDir);
        this.fileReaderService = new FileReaderService();
        this.executorService = Executors.newFixedThreadPool(nWorker);
        this.nWorker = nWorker;
        this.resultDir = resultDir;
    }

    public void searchAndWrite(String inputName, String outputName, Query query, int chunkSize) {

        LinkedBlockingQueue<Chunk> queue = splitFileAndRead(inputName, chunkSize);

        List<Future<Integer>> futures = runAsyncProcessChunk(queue, chunk -> {
            return executorService.submit(() -> {
                List<String> filtredLines = preProcessChunk(chunk.getLines(), query);
                if (filtredLines == null || filtredLines.isEmpty()) {
                    return 0;
                }

                Stream<String> resultLine = filtredLines.stream();
                resultLine = processChunkLineByLine(resultLine, query);
                writeChunkToFile(resultLine, chunk.getIndex(), outputName);
                return 0;
            });

        });

        Helpers.promiseAll(futures);
        mergeFile(outputName);
        executorService.shutdown();
    }

    private List<Future<Integer>> runAsyncProcessChunk(LinkedBlockingQueue<Chunk> queue,
            Function<Chunk, Future<Integer>> consumer) {
        boolean running = true;
        List<Future<Integer>> futures = new ArrayList<>();
        while (running) {
            try {
                Chunk chunk = queue.take();
                if (chunk.getIndex() == -1) {
                    running = false;
                    continue;
                }

                futures.add(consumer.apply(chunk));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return futures;
    }

    private LinkedBlockingQueue<Chunk> splitFileAndRead(String fileName, int chunkSize) {
        LinkedBlockingQueue<Chunk> queue = new LinkedBlockingQueue<>(nWorker + 2);
        Chunk endQueue = new Chunk(-1, null);

        executorService.submit(() -> {
            fileReaderService.readChunk(Path.of(fileName), chunkSize,
                    (lines, index) -> {
                        try {
                            queue.put(new Chunk(index, lines));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

            queue.add(endQueue);

        });

        return queue;
    }

    private List<String> preProcessChunk(List<String> chunk, Query query) {
        return processor.preProcess(chunk, query);

    }

    private Stream<String> processChunkLineByLine(Stream<String> chunk, Query query) {
        return this.processor.lineProcess(chunk, query);
    }

    private void writeChunkToFile(Stream<String> stream, int index, String outputName) {
        try {
            writerService.writeToFile(stream, index, outputName, resultDir);
        } catch (IOException e) {
            System.out.println("Write chunk to file failed, index:  " + index);
            e.printStackTrace();
        }
    }

    private void mergeFile(String resultFileName) {
        try {
            this.writerService.mergeTrunkFiles(resultFileName);
        } catch (IOException e) {
            System.out.println("Merge Files Failed");
            e.printStackTrace();
        }
    }

}
