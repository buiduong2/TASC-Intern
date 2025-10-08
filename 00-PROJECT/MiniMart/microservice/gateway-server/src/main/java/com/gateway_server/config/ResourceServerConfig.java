package com.gateway_server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
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
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwkSetUri(keySetUri)
                .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(converter))));

        http.authorizeExchange(exchanges -> exchanges
                .pathMatchers("/v1/auth/**").permitAll()
                .pathMatchers("/login").permitAll()
                .pathMatchers("/v1/payments/*/return").permitAll()
                .pathMatchers("/v1/payments/*/ipn").permitAll()
                .pathMatchers("/v1/categories/**").permitAll()
                .pathMatchers("/v1/products/**").permitAll()
                .pathMatchers("/v1/admin/users").authenticated()
                .anyExchange().authenticated());

        http.cors(cors -> cors.configurationSource(configurationSource));
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }
}
