package com.reader;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Merge kết quả từ nhiều thread reader thành một danh sách/log file output.
 */
public class ResultMerger {
    public void mergeFiles(List<String> inputFiles, String outputFile) {
        if (inputFiles == null || inputFiles.isEmpty())
            return;

        Path outputPath = Paths.get(outputFile);

        try (OutputStream out = Files.newOutputStream(outputPath)) {
            for (String fileName : inputFiles) {
                Path path = Paths.get(fileName);

                if (!Files.exists(path)) {
                    continue;
                }
                if (Files.size(path) == 0) {
                    Files.delete(path);
                    continue;
                }

                Files.copy(path, out);

                Files.delete(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
