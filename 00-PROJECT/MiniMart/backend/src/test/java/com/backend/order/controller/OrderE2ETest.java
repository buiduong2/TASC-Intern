package com.backend.order.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.backend.order.dto.req.OrderAddressReq;
import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.req.OrderItemReq;
import com.backend.order.dto.req.PaymentTransactionReq;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.dto.res.PaymentTransactionDTO;
import com.backend.order.model.Order;
import com.backend.order.model.PaymentMethod;
import com.backend.order.model.PaymentStatus;
import com.backend.order.model.TransactionStatus;
import com.backend.order.repository.OrderRepository;
import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.res.AuthRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@AutoConfigureMockMvc
public class OrderE2ETest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EntityManager entityManager;

    private VnpayReturnUrlMocker vnpayReturnUrlMocker;

    @Value("${custom.vnpay.hash-secret}")
    private String hashSecret;

    private LoginReq loginReq;

    private OrderCreateReq orderCreateReq;

    private PaymentTransactionReq paymentTransactionReq;

    @BeforeEach
    void setup() {
        loginReq = new LoginReq();
        loginReq.setUsername("root");
        loginReq.setPassword("12345678");

        orderCreateReq = new OrderCreateReq();
        orderCreateReq.setMessage("Xin vui lòng giao hàng nhanh");
        orderCreateReq.setShippingMethodId(1L);
        orderCreateReq.setPaymentMethod(PaymentMethod.CASH);

        // --- Address ---
        OrderAddressReq address = new OrderAddressReq();
        address.setFirstName("Nguyen");
        address.setLastName("Van A");
        address.setEmail("vana@example.com");
        address.setPhone("0912345678");
        address.setDetails("123 Đường ABC, Phường XYZ");
        address.setCity("Hà Nội");
        address.setArea("Hoàn Kiếm");
        orderCreateReq.setAddress(address);

        // --- Order Items ---
        OrderItemReq item1 = new OrderItemReq();
        item1.setProductId(5L);
        item1.setQuantity(2);

        OrderItemReq item2 = new OrderItemReq();
        item2.setProductId(17L);
        item2.setQuantity(1);

        LinkedHashSet<OrderItemReq> items = new LinkedHashSet<>();
        items.add(item1);
        items.add(item2);

        orderCreateReq.setOrderItems(items);

        paymentTransactionReq = new PaymentTransactionReq();
        paymentTransactionReq.setMethod("vnpay");

        System.out.println(hashSecret);
        vnpayReturnUrlMocker = new VnpayReturnUrlMocker(hashSecret);
    }

    @Test
    @Transactional
    @Rollback
    public void testOrderFlow() throws JsonProcessingException, Exception {

        // 1. Login
        MvcResult result = mockMvc.perform(post("/api/auth/login").content(om.writeValueAsString(loginReq))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AuthRes authRes = om.readValue(result.getResponse().getContentAsString(), AuthRes.class);

        assertThat(authRes.getAccessToken()).isNotNull();

        String accessToken = authRes.getAccessToken();
        assertThat(accessToken).isNotNull();
        entityManager.flush();
        entityManager.clear();

        // 2. Create Order
        MvcResult createOrder = mockMvc.perform(
                post("/api/orders")
                        .content(om.writeValueAsString(orderCreateReq))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        OrderDTO orderDTO = om.readValue(createOrder.getResponse().getContentAsString(), OrderDTO.class);
        long paymentId = orderDTO.getPaymentId();

        Order order = orderRepository.findById(orderDTO.getId()).orElseThrow();
        assertThat(order.getPayment()).isNotNull();
        assertThat(order.getPayment().getId()).isNotNull();
        assertThat(order.getPayment().getId()).isEqualTo(paymentId);

        assertThat(orderDTO.getId()).isNotNull();
        assertThat(paymentId).isNotNull();
        entityManager.flush();
        entityManager.clear();

        // 3. Create Payment Transaction

        MvcResult createPayment = mockMvc.perform(
                post("/api/payments/{paymentId}/transactions", paymentId)
                        .content("{\"method\" : \"VNPAY\"}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PaymentTransactionDTO transactionDTO = om.readValue(createPayment.getResponse().getContentAsString(),
                PaymentTransactionDTO.class);

        System.out.println(transactionDTO);
        assertThat(transactionDTO.getPaymentUrl()).isNotNull();
        // assertThat()

        entityManager.flush();
        entityManager.clear();

        // Return URRL
        String returnUrl = vnpayReturnUrlMocker.buildReturnUrl(transactionDTO.getPaymentUrl(),
                "/api/payments/vnpay/return");

        MvcResult returnPayment = mockMvc.perform(get(returnUrl))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        PaymentTransactionDTO returnDTO = om.readValue(returnPayment.getResponse().getContentAsString(),
                PaymentTransactionDTO.class);

        assertThat(returnDTO.getStatus()).isEqualTo(TransactionStatus.SUCCESS);

        entityManager.flush();
        entityManager.clear();

        Order actual = orderRepository.findById(order.getId()).orElseThrow();

        assertThat(actual.getPayment().getStatus()).isEqualTo(PaymentStatus.PAID);

    }
}
