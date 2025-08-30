package com.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * Ghi file -> nhận trunk ghi trunk_01 ... trunk_02.
 * 
 * Merge File khi cần
 */
public class FileWriterService {

    private final String resultDir;

    public FileWriterService(String resultDir) {
        this.resultDir = resultDir;
    }

    public void writeToFile(Stream<String> stream, int index, String outputName, String outputDir) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(outputDir + "\\" + outputName + "_" + index))) {
            stream.forEachOrdered(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void mergeTrunkFiles(String resultName) throws IOException {

        Path outputPath = Paths.get(resultName);
        try (Stream<Path> paths = Files.list(Paths.get(resultDir))) {
            try (OutputStream out = Files.newOutputStream(outputPath);) {

                paths.forEachOrdered(path -> {
                    try {

                        if (Files.size(path) == 0) {
                            Files.delete(path);
                            return;
                        }

                        Files.copy(path, out);
                        Files.delete(path);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
