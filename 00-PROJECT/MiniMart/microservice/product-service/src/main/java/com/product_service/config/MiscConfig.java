package com.product_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import com.cloudinary.Cloudinary;

@Configuration
@EnableAsync
@EnableConfigurationProperties(RedisConfig.class)
public class MiscConfig {

    @Bean
    Cloudinary cloudinary(@Value("${custom.cloudinary.url}") String url) {
        return new Cloudinary(url);
    }
}
