package com;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Main {

    final static Pattern LOG_PATTERN = Pattern
            .compile("^\\[(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})\\] \\[([A-Z]+)\\] \\[(\\S+)\\] - (.+)\\s*$");
    public final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    final static int TIME_INDEX = 1;
    final static int LEVEL_INDEX = 2;
    final static int SERVICE_INDEX = 3;
    final static int MESSAGE_INDEX = 4;
    final static String fileName = "demo.txt";

    public static void main(String[] args) throws IOException {
        long start = System.nanoTime();
        Query query = new Query();
        query.setService("PaymentService");
        query.setLevel("DEBUG");
        query.setKeyword("expired");
        query.setAfter("2025-08-26 03:58:54");
        query.setBefore("2025-08-26 03:59:42");

        Stream<String> lines = readFromFile(fileName);
        Predicate<Log> lastPredicate = buildPredicate(query);

        lines = lines.parallel().filter(line -> {
            Matcher matcher = LOG_PATTERN.matcher(line);
            if (matcher.matches()) {
                String timestamp = matcher.group(TIME_INDEX);
                String level = matcher.group(LEVEL_INDEX);
                String service = matcher.group(SERVICE_INDEX);
                String message = matcher.group(MESSAGE_INDEX);
                Log log = new Log(timestamp, level, service, message);
                return lastPredicate.test(log);
            }

            return true;
        });

        writeToFile("result", lines);

        long end = System.nanoTime();
        System.out.println("Executed in " + (end - start) / 1_000_000 + " ms");

    }

    public static Stream<String> readFromFile(String fileName) throws IOException {
        return Files.lines(Path.of(fileName));
    }

    public static void writeToFile(String fileName, Stream<String> lines) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of(fileName))) {

            lines.forEachOrdered(line -> {
                try {
                    writer.write(line);
                    writer.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static boolean isNotNullNorBlank(String str) {
        return str != null && !str.isBlank();
    }

    public static long parseTimestampLong(String timestamp) {
        if (timestamp == null || timestamp.isEmpty()) {
            throw new IllegalArgumentException("Timestamp is null or empty");
        }

        LocalDateTime dateTime = LocalDateTime.parse(timestamp, formatter);

        return dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
    }

    public static Predicate<Log> buildPredicate(Query query) {

        boolean hasService = isNotNullNorBlank(query.getService());
        boolean hasLevel = isNotNullNorBlank(query.getLevel());
        boolean hasRange = isNotNullNorBlank(query.getBefore()) && isNotNullNorBlank(query.getAfter());
        boolean hasKeyword = isNotNullNorBlank(query.getKeyword());
        String keyWordLowerCase = hasKeyword ? query.getKeyword().toLowerCase() : null;

        Long afterLong = hasRange ? parseTimestampLong(query.getAfter()) : null;
        Long beforeLong = hasRange ? parseTimestampLong(query.getBefore()) : null;

        Predicate<Log> servicePredicate = (log) -> {
            return query.getService().equals(log.getService());
        };

        Predicate<Log> levelPredicate = (log) -> {
            return query.getLevel().equals(log.getLevel());
        };

        Predicate<Log> timePredicate = (log) -> {
            long currentTimeLong = parseTimestampLong(log.getTimestamp());
            return currentTimeLong >= afterLong && currentTimeLong <= beforeLong;
        };

        Predicate<Log> keywordPredicate = (log) -> {
            return log.getMessage().toLowerCase().contains(keyWordLowerCase);
        };

        Predicate<Log> combinePredicate = (objs) -> true;

        if (hasService) {
            combinePredicate = combinePredicate.and(servicePredicate);
        }
        if (hasLevel) {
            combinePredicate = combinePredicate.and(levelPredicate);
        }
        if (hasRange) {
            combinePredicate = combinePredicate.and(timePredicate);
        }
        if (hasKeyword) {
            combinePredicate = combinePredicate.and(keywordPredicate);
        }
        return combinePredicate;
    }

}