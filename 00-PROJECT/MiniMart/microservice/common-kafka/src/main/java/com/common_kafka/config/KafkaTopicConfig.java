package com.common_kafka.config;

import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {
    private NewTopic buildDefaultTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(3) // Số lượng partitions
                .replicas(1) // Số lượng replicas (nên là 3 trong môi trường Production)
                .configs(Map.of(
                        "cleanup.policy", "delete", // Xóa tin nhắn sau thời gian retention
                        "retention.ms", "86400000")) // Giữ tin nhắn trong 24 giờ (86,400,000 ms)
                .build();
    }
    // ------------------------------------------------------------------------
    // SALES (ORDER & CART)
    // ------------------------------------------------------------------------

    @Bean
    public NewTopic salesOrderEvents() {
        return buildDefaultTopic(KafkaTopics.SALES_ORDER_EVENTS);
    }

    @Bean
    public NewTopic salesOrderInitCommands() {
        return buildDefaultTopic(KafkaTopics.SALES_ORDER_INIT_COMMANDS);
    }

    @Bean
    public NewTopic salesOrderCancelCommands() {
        return buildDefaultTopic(KafkaTopics.SALES_ORDER_CANCEL_COMMANDS);
    }

    @Bean
    public NewTopic salesOrderCompensation() {
        return buildDefaultTopic(KafkaTopics.SALES_ORDER_COMPENSATION);
    }

    @Bean
    public NewTopic salesCartEvents() {
        return buildDefaultTopic(KafkaTopics.SALES_CART_EVENTS);
    }

    // ------------------------------------------------------------------------
    // CATALOG & PRODUCT
    // ------------------------------------------------------------------------

    @Bean
    public NewTopic catalogProductValidation() {
        return buildDefaultTopic(KafkaTopics.CATALOG_PRODUCT_VALIDATION);
    }

    @Bean
    public NewTopic catalogProductEvents() {
        return buildDefaultTopic(KafkaTopics.CATALOG_PRODUCT_EVENTS);
    }

    // ------------------------------------------------------------------------
    // SUPPLY CHAIN (INVENTORY)
    // ------------------------------------------------------------------------

    @Bean
    public NewTopic supplyInventoryReservation() {
        return buildDefaultTopic(KafkaTopics.SUPPLY_INVENTORY_RESERVATION);
    }

    @Bean
    public NewTopic supplyInventoryAllocation() {
        return buildDefaultTopic(KafkaTopics.SUPPLY_INVENTORY_ALLOCATION);
    }

    @Bean
    public NewTopic supplyInventoryCompensation() {
        return buildDefaultTopic(KafkaTopics.SUPPLY_INVENTORY_COMPENSATION);
    }

    // ------------------------------------------------------------------------
    // FINANCE & PAYMENT
    // ------------------------------------------------------------------------

    @Bean
    public NewTopic financePaymentRequest() {
        return buildDefaultTopic(KafkaTopics.FINANCE_PAYMENT_REQUEST);
    }

    @Bean
    public NewTopic financePaymentEvents() {
        return buildDefaultTopic(KafkaTopics.FINANCE_PAYMENT_EVENTS);
    }
}
