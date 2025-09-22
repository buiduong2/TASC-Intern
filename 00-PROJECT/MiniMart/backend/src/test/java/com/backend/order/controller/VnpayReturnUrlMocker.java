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
        //"vnp_Amount=458943000&vnp_BankCode=NCB&vnp_BankTranNo=VNP1758264086270&vnp_CardType=ATM&vnp_OrderInfo=Pay%2Bfor%2Border%2Bid%2B%253D1502&vnp_PayDate=20250919134126&vnp_ResponseCode=00&vnp_TmnCode=C270LVZU&vnp_TransactionNo=86270&vnp_TransactionStatus=00&vnp_TxnRef=11f12bae5f554e75a6e9"
        //"vnp_Amount=458943000&vnp_BankCode=NCB&vnp_BankTranNo=VNP1758264187803&vnp_CardType=ATM&vnp_OrderInfo=Pay+for+order+id+%3D1602&vnp_PayDate=20250919134307&vnp_ResponseCode=00&vnp_TmnCode=C270LVZU&vnp_TransactionNo=187803&vnp_TransactionStatus=00&vnp_TxnRef=4c3a5a380fc74e769c43"
        //"vnp_Amount=458943000&vnp_BankCode=NCB&vnp_BankTranNo=VNP1758264187803&vnp_CardType=ATM&vnp_OrderInfo=Pay+for+order+id+%3D1602&vnp_PayDate=20250919134307&vnp_ResponseCode=00&vnp_TmnCode=C270LVZU&vnp_TransactionNo=187803&vnp_TransactionStatus=00&vnp_TxnRef=4c3a5a380fc74e769c43"
        //"vnp_Amount=458943000&vnp_BankCode=NCB&vnp_BankTranNo=VNP1758264522383&vnp_CardType=ATM&vnp_OrderInfo=Pay+for+order+id+%3D1802&vnp_PayDate=20250919134842&vnp_ResponseCode=00&vnp_TmnCode=C270LVZU&vnp_TransactionNo=522383&vnp_TransactionStatus=00&vnp_TxnRef=686decef66584ce5a642"


        String hashData = VnpayUtils.buildHashData(params);
        String secureHash = VnpayUtils.hmacSHA512(hashSecret, hashData);

        // Build returnUrl
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