package com.order_service.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.order_service.dto.res.GatewayResponseData;
import com.order_service.dto.res.PaymentGatewayCreateDTO;
import com.order_service.enums.IpnResponseType;
import com.order_service.model.PaymentTransaction;
import com.order_service.service.PaymentGateway;
import com.order_service.utils.VnpayUtils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component("vnpay")
@RequiredArgsConstructor
public class VnPayGateway implements PaymentGateway {

    @Value("${custom.vnpay.merchant-ip}")
    private String merchantIp;

    @Value("${custom.vnpay.zone-id}")
    private String zoneId = "Asia/Ho_Chi_Minh";

    @Value("${custom.vnpay.pay-url}")
    private String payUrl;

    @Value("${custom.vnpay.return-url}")
    private String returnUrl;

    @Value("${custom.vnpay.query-dr-url}")
    private String queryDrUrl;

    @Value("${custom.vnpay.tmn-code}")
    private String tmnCode;

    @Value("${custom.vnpay.hash-secret}")
    private String hashSecret;

    @Override
    public PaymentGatewayCreateDTO createTransaction(String orderInfo, String txnRef, BigDecimal amount,
            HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        ZoneId zone = ZoneId.of(zoneId);
        LocalDateTime now = LocalDateTime.now(zone);
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_Amount", amount.multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", returnUrl);
        params.put("vnp_IpAddr", request.getRemoteAddr());
        params.put("vnp_CreateDate", VnpayUtils.toVnpDate(now));
        params.put("vnp_ExpireDate", VnpayUtils.toVnpDate(now.plusMinutes(15)));

        String hashData = VnpayUtils.buildHashData(params);
        String secureHash = VnpayUtils.hmacSHA512(hashSecret, hashData);
        String query = VnpayUtils.buildQuery(params) + "&vnp_SecureHash=" + secureHash;
        String redirectUrl = payUrl + "?" + query;

        return new PaymentGatewayCreateDTO(redirectUrl, params.get("vnp_TxnRef"));
    }

    @Override
    public GatewayResponseData verifyReturn(Map<String, String> params) {
        return verifySignature(params, VnpayUtils::buildHashData);
    }

    @Override
    public GatewayResponseData verifyIpn(Map<String, String> params) {
        return verifySignature(params, VnpayUtils::buildHashData);
    }

    private GatewayResponseData verifySignature(Map<String, String> params,
            Function<Map<String, String>, String> buildHashDataFunc) {

        String receivedHash = params.get("vnp_SecureHash");
        String hashData = buildHashDataFunc.apply(params);
        String calculatedHash = VnpayUtils.hmacSHA512(hashSecret, hashData);

        if (!calculatedHash.equalsIgnoreCase(receivedHash)) {
            return GatewayResponseData.builder()
                    .issignatureValid(false)
                    .build();
        }

        String txnRef = params.get("vnp_TxnRef");
        String gatewayTxnId = params.get("vnp_TransactionNo");
        BigDecimal amount = new BigDecimal(params.get("vnp_Amount"))
                .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN);
        String orderInfo = params.get("vnp_OrderInfo");
        String payDateStr = Optional.ofNullable(params.get("vnp_PayDate"))
                .orElse(params.get("vnp_TransactionDate"));

        LocalDateTime paidAt = VnpayUtils.parseVnpDate(payDateStr);

        boolean success = "00".equals(params.get("vnp_ResponseCode"));
        System.out.println(params.get("vnp_ResponseCode"));

        return GatewayResponseData.builder()
                .paidAt(paidAt)
                .txnRef(txnRef)
                .gatewayTxnId(gatewayTxnId)
                .amount(amount)
                .orderInfo(orderInfo)
                .success(success)
                .issignatureValid(true)
                .build();
    }

    @Override
    public Object getIpnResponse(IpnResponseType type) {
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
    public GatewayResponseData refund(PaymentTransaction paymentTransaction, HttpServletRequest servletRequest,
            long userId) {
        String orderInfo = "Refund for order id =" + paymentTransaction.getPayment().getOrderId();
        ZoneId zone = ZoneId.of(zoneId); // GMT+7
        LocalDateTime now = LocalDateTime.now(zone);
        Map<String, String> params = new HashMap<>();
        params.put("vnp_RequestId", String.valueOf(System.currentTimeMillis()));
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "refund");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_TransactionType", "02");
        params.put("vnp_TxnRef", paymentTransaction.getTxnRef());
        params.put("vnp_Amount",
                paymentTransaction.getAmount().multiply(BigDecimal.valueOf(100)).toBigInteger().toString());
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_TransactionNo", paymentTransaction.getGatewayTxnId());
        params.put("vnp_TransactionDate", VnpayUtils.toVnpDate(paymentTransaction.getCreatedAt()));
        params.put("vnp_CreateBy", String.valueOf(userId));
        params.put("vnp_CreateDate", VnpayUtils.toVnpDate(now));
        params.put("vnp_IpAddr", servletRequest.getRemoteAddr());

        String hashData = VnpayUtils.buildHashDataRefund(params);
        String secureHash = VnpayUtils.hmacSHA512(hashSecret, hashData);

        HttpHeaders headers = new HttpHeaders();
        Map<String, String> body = new HashMap<>(params);
        body.put("vnp_SecureHash", secureHash);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
        Map<String, String> response = (Map<String, String>) restTemplate.postForObject(queryDrUrl, request, Map.class);

        return verifySignature(response, VnpayUtils::buildHashDataRefundResponse);
    }

    @Override
    public GatewayResponseData queryDr(String txnRef, String orderInfo, LocalDateTime createdAt) {

        ZoneId zone = ZoneId.of(zoneId); // GMT+7
        LocalDateTime now = LocalDateTime.now(zone);
        Map<String, String> params = new HashMap<>();
        params.put("vnp_RequestId", String.valueOf(System.currentTimeMillis()));
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "querydr");
        params.put("vnp_TmnCode", tmnCode);
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_TransactionDate", VnpayUtils.toVnpDate(createdAt));
        params.put("vnp_CreateDate", VnpayUtils.toVnpDate(now));
        params.put("vnp_IpAddr", merchantIp);

        String hashData = VnpayUtils.buildHashDataQueryDR(params);
        String secureHash = VnpayUtils.hmacSHA512(hashSecret, hashData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>(params);
        body.put("vnp_SecureHash", secureHash);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        @SuppressWarnings("unchecked")
        Map<String, String> response = (Map<String, String>) restTemplate.postForObject(queryDrUrl, request, Map.class);

        return verifySignature(response, VnpayUtils::buildHashDataQueryDRResponse);
    }
}
