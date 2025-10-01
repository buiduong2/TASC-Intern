package com.gateway_server.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
public class WebConfig {

    @Value("${custom.client.origins}")
    private List<String> clientOrigins;

    @Bean
    CorsConfigurationSource configurationSource() {

        if (clientOrigins == null || clientOrigins.isEmpty()) {
            clientOrigins = new ArrayList<>();
        }

        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(clientOrigins);
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTION"));
            config.setAllowedHeaders(List.of("*"));
            return config;
        };
    }
}