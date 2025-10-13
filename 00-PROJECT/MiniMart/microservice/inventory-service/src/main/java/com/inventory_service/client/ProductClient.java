package com.inventory_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.inventory_service.client.fallback.ProductFallbackFactory;
import com.inventory_service.dto.req.ProductCheckExistsReq;
import com.inventory_service.dto.res.ProductCheckExistsRes;

@FeignClient(name = "product-service", fallbackFactory = ProductFallbackFactory.class)
public interface ProductClient {

    @PostMapping("/v1/internal/products/validate-existence")
    ProductCheckExistsRes checkProductExsitsByIds(@RequestBody ProductCheckExistsReq req);
}
