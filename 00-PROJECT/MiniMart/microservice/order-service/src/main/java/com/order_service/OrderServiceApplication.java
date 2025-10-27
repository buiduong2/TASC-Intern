package com.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.order_service.service.impl.OrderSagaCleanupService;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(OrderServiceApplication.class, args);

        OrderSagaCleanupService cleanupService = context.getBean(OrderSagaCleanupService.class);
        cleanupService.processAwaitingCancellation();
    }

}
