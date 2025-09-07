package com.backend.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtils {

    public static String prettyJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Object obj = mapper.readValue(json, Object.class);
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return json;
        }
    }
}
