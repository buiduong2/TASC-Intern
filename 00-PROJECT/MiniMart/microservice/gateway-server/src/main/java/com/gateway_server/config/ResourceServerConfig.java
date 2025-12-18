package com.gateway_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

import com.gateway_server.security.CustomJwtAuthenticationConverter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ResourceServerConfig {

    @Value("${custom.auth.key_set_uri}")
    private String keySetUri;

    final CustomJwtAuthenticationConverter converter;

    final CorsConfigurationSource configurationSource;

    @Bean
    @Order(1)
    public SecurityWebFilterChain publicEndpoints(ServerHttpSecurity http) {
        // SỬ DỤNG ServerWebExchangeMatchers.matchers() để gom nhóm TẤT CẢ các matcher
        ServerWebExchangeMatcher publicMatchers = ServerWebExchangeMatchers.matchers(
                ServerWebExchangeMatchers.pathMatchers(
                        "v1/categories/**",
                        "v1/products/**",
                        "/css/**", "/js/**", "/images/**", "/favicon.ico",
                        "/oauth2/authorize/**",
                        "/oauth2/token/**",
                        "/login", // Form login (GET và POST)
                        "/v1/auth/**" // Auth APIs
                ),
                ServerWebExchangeMatchers.pathMatchers("/v1/payments/*/ipn"),
                ServerWebExchangeMatchers.pathMatchers("/v1/payments/*/return"));

        return http
                // Truyền MỘT đối số composite matcher
                .securityMatcher(publicMatchers)
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain securedEndpoints(ServerHttpSecurity http) {
        http
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwkSetUri(keySetUri)))
                .authorizeExchange(auth -> auth.anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}
