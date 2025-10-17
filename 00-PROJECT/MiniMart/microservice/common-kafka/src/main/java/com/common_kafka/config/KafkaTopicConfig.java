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
    // TOPICS LIÊN QUAN ĐẾN SALE (ORDER & CART)
    // ------------------------------------------------------------------------

    @Bean
    NewTopic topicSalesOrderEvents() {
        return buildDefaultTopic(KafkaTopics.SALES_ORDER_EVENTS);
    }

    @Bean
    NewTopic topicSalesCartEvents() {
        return buildDefaultTopic(KafkaTopics.SALES_CART_EVENTS);
    }

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN CATALOG & PRODUCT
    // ------------------------------------------------------------------------

    @Bean
    NewTopic topicCatalogProductEvents() {
        return buildDefaultTopic(KafkaTopics.CATALOG_PRODUCT_EVENTS);
    }

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN SUPPLY CHAIN (INVENTORY)
    // ------------------------------------------------------------------------

    @Bean
    NewTopic topicSupplyInventoryReservation() {
        return buildDefaultTopic(KafkaTopics.SUPPLY_INVENTORY_RESERVATION);
    }

    @Bean
    NewTopic topicSupplyInventoryAllocation() {
        return buildDefaultTopic(KafkaTopics.SUPPLY_INVENTORY_ALLOCATION);
    }

    // ------------------------------------------------------------------------
    // TOPICS LIÊN QUAN ĐẾN FINANCE & PAYMENT
    // ------------------------------------------------------------------------

    @Bean
    NewTopic topicFinancePaymentEvents() {
        return buildDefaultTopic(KafkaTopics.FINANCE_PAYMENT_EVENTS);
    }
}
