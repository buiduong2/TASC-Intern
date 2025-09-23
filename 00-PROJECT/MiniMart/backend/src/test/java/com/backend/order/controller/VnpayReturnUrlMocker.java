package com.backend.order.controller;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.backend.order.utils.VnpayUtils;

public class VnpayReturnUrlMocker {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final String hashSecret;

    public VnpayReturnUrlMocker(String hashSecret) {
        this.hashSecret = hashSecret;
    }

    public String buildReturnUrl(String payUrl, String baseReturnUrl) {
        Map<String, String> params = parsePayUrl(payUrl);

        // Bỏ các field không có trong returnUrl thật
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");
        params.remove("vnp_CreateDate");
        params.remove("vnp_ExpireDate");
        params.remove("vnp_IpAddr");
        params.remove("vnp_ReturnUrl");
        params.remove("vnp_Command");
        params.remove("vnp_Version");
        params.remove("vnp_CurrCode");
        params.remove("vnp_Locale");
        params.remove("vnp_OrderType");

        // Thêm các field callback
        params.put("vnp_BankCode", "NCB");
        params.put("vnp_BankTranNo", "VNP" + System.currentTimeMillis());
        params.put("vnp_CardType", "ATM");
        params.put("vnp_PayDate", LocalDateTime.now().format(DATE_FORMAT));
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_TransactionNo", String.valueOf(System.currentTimeMillis() % 1_000_000));
        params.put("vnp_TransactionStatus", "00");

        // Ký lại hash
        String hashData = VnpayUtils.buildHashData(params);
        String secureHash = VnpayUtils.hmacSHA512(hashSecret, hashData);

        String query = VnpayUtils.buildQuery(params) + "&vnp_SecureHash=" + secureHash;

        return baseReturnUrl + "?" + query;
    }

    private Map<String, String> parsePayUrl(String payUrl) {
        URI uri = URI.create(payUrl);
        String query = uri.getQuery();
        return Arrays.stream(query.split("&"))
                .map(p -> p.split("=", 2))
                .collect(Collectors.toMap(
                        kv -> URLDecoder.decode(kv[0], StandardCharsets.US_ASCII),
                        kv -> kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.US_ASCII) : "",
                        (a, b) -> b,
                        LinkedHashMap::new));
    }
}