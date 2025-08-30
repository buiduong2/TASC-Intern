package com.log;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.util.Helpers;
import com.util.LogParsers;
import com.util.LogPredicates;

/**
 * Xử lý logic liên quan đế tiêu chí tìm kiếm
 */
public class LogProcessor {

    private final LogParsers logParsers;

    public LogProcessor(LogParsers logParsers) {
        this.logParsers = logParsers;
    }

    /**
     * Xử lý theo GROUP
     */
    public List<String> preProcess(List<String> chunk, Query query) {
        return filterByTimestamp(chunk, query);
    }

    private List<String> filterByTimestamp(List<String> lines, Query query) {
        String after = query.getAfter();
        String before = query.getBefore();
        if (Helpers.isNullOrBlank(before) || Helpers.isNullOrBlank(after)) {
            return lines;
        }

        // FIND LEFT
        long afterTarget = logParsers.parseTimestampLong(after);

        int ceilIndex = Helpers.findCeilIndex(lines, afterTarget, logParsers::parseLineToTimestamp,
                Comparator.naturalOrder(), 0, lines.size() - 1);

        if (ceilIndex == -1) {
            return null;
        }

        // Find RIGHT
        long beforeTarget = logParsers.parseTimestampLong(before);

        int floorIndex = Helpers.findFloorIndex(lines, beforeTarget,
                logParsers::parseLineToTimestamp,
                Comparator.naturalOrder(), ceilIndex, lines.size() - 1);
        if (floorIndex == -1) {
            return null;
        }
        return lines.subList(ceilIndex, floorIndex + 1);
    }

    /**
     * Xử lý theo LINE
     */
    public Stream<String> lineProcess(Stream<String> chunk, Query query) {
        Predicate<Log> preidcate = LogPredicates.buildPredicate(query);
        return chunk.filter(line -> {
            Log log = this.logParsers.parserToLogIgnoreTimestamp(line);
            return preidcate.test(log);
        });
    }

}
