package com.backend.order.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class VnpayUtils {
    /** Sinh HMAC SHA512 */
    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            hmac512.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            byte[] result = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error while generating HMAC SHA512", ex);
        }
    }

    /** Build rawData để ký hash (phải encode key và value) */
    public static String buildHashData(Map<String, String> params) {
        Map<String, String> sorted = new TreeMap<>(params);
        sorted.remove("vnp_SecureHash");
        sorted.remove("vnp_SecureHashType");

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if (sb.length() > 0)
                sb.append("&");
            sb.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
        }
        return sb.toString();
    }

    /** Build query string để redirect */
    public static String buildQuery(Map<String, String> params) {
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : new TreeMap<>(params).entrySet()) {
            if (query.length() > 0)
                query.append("&");
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.US_ASCII))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII));
        }
        return query.toString();
    }

    private static final DateTimeFormatter VNP_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String toVnpDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("dateTime cannot be null");
        }
        return dateTime.format(VNP_DATE_FORMATTER);
    }

    public static Map<String, String> parseQuery(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] parts = param.split("=", 2);
            String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
            result.put(key, value);
        }
        return result;
    }
}
