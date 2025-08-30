package com.util;

import java.util.function.Predicate;

import com.log.Log;
import com.log.Query;

public class LogPredicates {

    public static Predicate<Log> isService(String service) {
        return log -> log.getService().equals(service);
    }

    public static Predicate<Log> isLevel(String level) {
        return log -> log.getLevel().equals(level);
    }

    public static Predicate<Log> isKeyWordContainIgnoreCase(String keyword) {
        String keyWordLowerCase = keyword.toLowerCase();
        return log -> log.getMessage().toLowerCase().contains(keyWordLowerCase);
    }

    public static Predicate<Log> buildPredicate(Query query) {
        Predicate<Log> combine = log -> true;

        if (!Helpers.isNullOrBlank(query.getLevel())) {
            combine = combine.and(isLevel(query.getLevel()));
        }
        if (!Helpers.isNullOrBlank(query.getService())) {
            combine = combine.and(isService(query.getService()));
        }

        if (!Helpers.isNullOrBlank(query.getKeyword())) {
            combine = combine.and(isKeyWordContainIgnoreCase(query.getKeyword()));
        }

        return combine;
    }
}
