package com.admin_bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import feign.RequestInterceptor;
import io.github.resilience4j.common.bulkhead.configuration.ThreadPoolBulkheadConfigCustomizer;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class CloudConfig {

    private final String[] CUSTOM_HEADERS = { "x-user-id", "x-user-roles" };

    @Bean
    public RequestInterceptor forwardCustomHeaderInterceptor() {
        return template -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                for (String headerName : CUSTOM_HEADERS) {
                    String headerValue = request.getHeader(headerName);
                    if (headerValue != null && !headerValue.isEmpty()) {
                        template.header(headerName, headerValue);
                    }
                }
            }
        };
    }

    // @SuppressWarnings("unchecked")
    // @Bean
    // ThreadPoolBulkheadConfigCustomizer requestContextBulkheadCustomizer() {
    //     return ThreadPoolBulkheadConfigCustomizer.of("default",
    //             builder -> builder.contextPropagator(RequestHeaderContextPropagator.class));
    // }

}
