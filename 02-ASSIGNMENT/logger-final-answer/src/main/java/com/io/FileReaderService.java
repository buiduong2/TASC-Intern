package com.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Đọc File, nhưng chia trunk
 */
public class FileReaderService {

    public void readChunk(Path file, int chunkSize, BiConsumer<List<String>, Integer> chunkConsumer) {
        List<String> lines = new ArrayList<>();
        int index = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);

                if (lines.size() >= chunkSize) {
                    chunkConsumer.accept(new ArrayList<>(lines), index);
                    lines.clear();
                    index++;
                }

            }

            if (!lines.isEmpty()) {
                chunkConsumer.accept(new ArrayList<>(lines), index);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
