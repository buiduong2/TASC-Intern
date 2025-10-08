package com.admin_bff.client;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.admin_bff.client.fallback.AuthFallbackFactory;
import com.admin_bff.config.AuthClientConfiguration;
import com.admin_bff.dto.res.UserAuthDTO;
import com.common.dto.PageResponseDTO;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;

@FeignClient(name = "authentication-service", fallbackFactory = AuthFallbackFactory.class, configuration = AuthClientConfiguration.class)
public interface AuthClient {

    @Bulkhead(name = "authentication-service")
    @GetMapping("/v1/admin/users/summary")
    PageResponseDTO<UserAuthDTO> getUsers(@RequestParam Map<String, ?> params);
}
