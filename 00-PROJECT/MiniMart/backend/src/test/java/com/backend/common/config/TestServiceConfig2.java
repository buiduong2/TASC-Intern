package com.backend.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.backend.inventory.utils.PurchaseOrderConverter;

@Configuration

public class TestServiceConfig2 {

    @Bean
    PurchaseOrderConverter purchaseOrderConverter() {
        return new PurchaseOrderConverter();
    }
}
