package com.product_service.config;

import static com.product_service.utils.CacheUtils.CATEGORY_DETAIL;
import static com.product_service.utils.CacheUtils.CATEGORY_DETAIL_TTL;
import static com.product_service.utils.CacheUtils.CATEGORY_SUMMARY;
import static com.product_service.utils.CacheUtils.CATEGORY_SUMMARY_TTL;
import static com.product_service.utils.CacheUtils.PRODUCT_DETAIL;
import static com.product_service.utils.CacheUtils.PRODUCT_DETAIL_TTL;
import static com.product_service.utils.CacheUtils.PRODUCT_ID_BY_CATEGORY;
import static com.product_service.utils.CacheUtils.PRODUCT_ID_BY_CATEGORY_TTL;
import static com.product_service.utils.CacheUtils.PRODUCT_RELATE_ID_BY_PRODUCT;
import static com.product_service.utils.CacheUtils.PRODUCT_RELATE_ID_BY_PRODUCT_TTL;

import java.time.Duration;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.BatchStrategies;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.product_service.utils.CacheUtils;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(mapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(10)).disableCachingNullValues()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .computePrefixWith(CacheUtils.cacheKeyPrefix);

        Map<String, RedisCacheConfiguration> singletonMap = Map.of(
                PRODUCT_DETAIL, defaultConfig.entryTtl(PRODUCT_DETAIL_TTL),
                PRODUCT_ID_BY_CATEGORY, defaultConfig.entryTtl(PRODUCT_ID_BY_CATEGORY_TTL),
                PRODUCT_RELATE_ID_BY_PRODUCT, defaultConfig.entryTtl(PRODUCT_RELATE_ID_BY_PRODUCT_TTL),
                CATEGORY_SUMMARY, defaultConfig.entryTtl(CATEGORY_SUMMARY_TTL),
                CATEGORY_DETAIL, defaultConfig.entryTtl(CATEGORY_DETAIL_TTL)

        );
        return RedisCacheManager
                .builder(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory, BatchStrategies.scan(1000)))
                .cacheDefaults(defaultConfig)
                .transactionAware()
                .withInitialCacheConfigurations(singletonMap)
                .build();
    }

    @Bean
    RedisTemplate<String, Object> productDetailRedisTemplate(RedisConnectionFactory connectionFactory) {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY);

        mapper.enable(DeserializationFeature.USE_LONG_FOR_INTS);

        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(mapper);
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setValueSerializer(valueSerializer);
        template.setKeySerializer(new StringRedisSerializer());
        template.afterPropertiesSet();

        return template;
    }

}
