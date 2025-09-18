package com.backend.order.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.backend.order.dto.res.PaymentGatewayCreateDTO;
import com.backend.order.dto.res.GatewayResponseData;
import com.backend.order.dto.res.PaymentTransactionDTO;
import com.backend.order.service.PaymentGateway;
import com.backend.order.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@Component("vnpay")
public class VnPayGateway implements PaymentGateway {

    @Value("${custom.vnpay.pay-url}")
    private String payUrl;

    @Value("${custom.vnpay.return-url}")
    private String returnUrl;

    @Value("${custom.vnpay.tmn-code}")
    private String tmnCode;

    @Value("${custom.vnpay.hash-secret}")
    private String hashSecret;

    @Override
    public PaymentGatewayCreateDTO createTransaction(String orderInfo, String txnRef, double amount,
            HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", String.valueOf((long) (amount * 100)));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", request.getRemoteAddr());

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        params.put("vnp_CreateDate", formatter.format(cld.getTime()));
        cld.add(Calendar.MINUTE, 15);
        params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

        String hashData = buildHashData(params);
        String secureHash = hmacSHA512(hashSecret, hashData);
        String query = buildQuery(params) + "&vnp_SecureHash=" + secureHash;
        String redirectUrl = payUrl + "?" + query;

        return new PaymentGatewayCreateDTO(redirectUrl, params.get("vnp_TxnRef"));
    }

    @Override
    public GatewayResponseData verifyReturn(Map<String, String> params) {
        return verifySignature(params);
    }

    @Override
    public GatewayResponseData verifyIpn(Map<String, String> params) {
        return verifySignature(params);
    }

    private GatewayResponseData verifySignature(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        String hashData = buildHashData(params);
        String calculatedHash = hmacSHA512(hashSecret, hashData);

        if (!calculatedHash.equalsIgnoreCase(receivedHash)) {
            return GatewayResponseData.builder()
                    .issignatureValid(false)
                    .build();
        }

        String txnRef = params.get("vnp_TxnRef");
        String gatewayTxnId = params.get("vnp_TransactionNo");
        double amount = Double.parseDouble(params.get("vnp_Amount")) / 100.0;
        String orderInfo = params.get("vnp_OrderInfo");
        boolean success = "00".equals(params.get("vnp_ResponseCode"));

        return GatewayResponseData.builder()
                .txnRef(txnRef)
                .gatewayTxnId(gatewayTxnId)
                .amount(amount)
                .orderInfo(orderInfo)
                .success(success)
                .issignatureValid(true)
                .build();
    }

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

    @Override
    public Object getIpnResponse(PaymentService.IpnResponseType type) {
        switch (type) {
            case SIGNATURE_NOT_VALID:
                return Map.of("RspCode", "97", "Message", "Invalid Signature");
            case ORDER_NOT_FOUND:
                return Map.of("RspCode", "01", "Message", "Order not found");
            case AMOUNT_NOT_VALID:
                return Map.of("RspCode", "04", "Message", "Invalid amount");
            case STATUS_NOT_VALID:
                return Map.of("RspCode", "02", "Message", "Order already confirmed");
            case USER_NOT_REDIRECTED:
            default:
                return Map.of("RspCode", "00", "Message", "Confirm Success");
        }
    }

    @Override
    public PaymentTransactionDTO refund(Long paymentId, Long transactionId, Long adminId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'refund'");
    }
}
