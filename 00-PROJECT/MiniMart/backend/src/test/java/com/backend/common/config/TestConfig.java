package com.backend.common.config;

import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.backend.inventory.mapper.PurchaseItemMapper;
import com.backend.inventory.mapper.PurchaseMapper;
import com.backend.inventory.repository.PurchaseRepository;
import com.backend.inventory.service.PurchaseService;
import com.backend.inventory.service.impl.PurchaseServiceImpl;
import com.backend.product.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableJpaAuditing
public class TestConfig {
    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;

    @Bean
    PurchaseService purchaseService() {
        return new PurchaseServiceImpl(purchaseRepository, productRepository, purchaseMapper(),
                applicationEventPublisher());
    }

    @Bean("mockPublisher")
    ApplicationEventPublisher applicationEventPublisher() {
        return Mockito.mock(ApplicationEventPublisher.class);
    }

    @Bean
    PurchaseItemMapper purchaseItemMapper() {
        return Mappers.getMapper(PurchaseItemMapper.class);
    }

    @Bean
    PurchaseMapper purchaseMapper() {
        return Mappers.getMapper(PurchaseMapper.class);
    }

}
