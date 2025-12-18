package com.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.sales.order.OrderProductValidationRequestedEvent;

@FeignClient("product-service")
public interface ProductClient {

    @PostMapping("/v1/internal/products/order-validation")
    ProductValidationPassedEvent validateOrder(@RequestBody OrderProductValidationRequestedEvent req);
}
