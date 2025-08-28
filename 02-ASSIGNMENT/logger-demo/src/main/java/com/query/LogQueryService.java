package com.query;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.model.LogMeta;
import com.reader.LogFileReader;
import com.utils.Helpers;
import com.utils.LogParsers;

/**
 * Thực hiện nhiều LogQuery song song
 * 
 * Merge kết quả
 */
public class LogQueryService {

    private final LogQuery query;
    private final int trunkSize;

    private final LogFileReader logFileReader;

    public LogQueryService(LogQuery query, int trunkSize, String fileName,
            LogFileReader logFileReader) {
        this.query = query;
        this.trunkSize = trunkSize;
        this.logFileReader = logFileReader;
    }

    public void execute(QueryCondition condition) {
        List<LogMeta> logMetas = query.execute(condition);
        List<List<LogMeta>> logMetaTrunks = Helpers.trunk(logMetas, trunkSize);

        final Optional<String> lowerCaseKeyword = Optional.ofNullable(condition.getKeyword())
                .map(String::toLowerCase);
        Predicate<String> messagePredicate = line -> isMessageMatch(line, lowerCaseKeyword);

        logFileReader.readAndWriteMatchTrunk(logMetaTrunks, messagePredicate, "_result");

        

    }

    private boolean isMessageMatch(String line, Optional<String> keywordOpt) {
        if (!keywordOpt.isPresent()) {
            return true;
        }
        return isMessageMatch(line, keywordOpt.get().toLowerCase());
    }

    private boolean isMessageMatch(String line, String keywordLowerCase) {
        String msg = LogParsers.parseMessage(line);
        return msg.toLowerCase().contains(keywordLowerCase);
    }
}