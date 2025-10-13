package com.product_service.utils;

import java.util.Collection;
import java.util.Map;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RedisUtils {

    @SuppressWarnings({ "null", "unchecked" })
    public <E> void batchAddString(RedisTemplate<String, E> redisTemplate, Map<String, E> mayEByKey, long ttl) {
        RedisSerializer<String> keySer = redisTemplate.getStringSerializer();
        RedisSerializer<E> valSer = (RedisSerializer<E>) redisTemplate
                .getValueSerializer();

        redisTemplate.executePipelined((RedisCallback<?>) conn -> {
            for (Map.Entry<String, E> entry : mayEByKey.entrySet()) {
                byte[] k = keySer.serialize(entry.getKey());
                byte[] v = valSer.serialize(entry.getValue());
                conn.stringCommands().setEx(k, ttl, v);
            }

            return null;
        });
    }

    @SuppressWarnings({ "null", "unchecked" })
    public <K, V> long batchAddSet(RedisTemplate<K, V> template, Map<K, ? extends Collection<V>> data,
            long ttlSeconds) {
        RedisSerializer<K> keySer = (RedisSerializer<K>) template.getKeySerializer();
        RedisSerializer<V> valSer = (RedisSerializer<V>) template.getValueSerializer();
        long total = data.values().stream().mapToLong(Collection::size).sum();
        template.executePipelined((RedisCallback<?>) conn -> {
            for (Map.Entry<K, ? extends Collection<V>> e : data.entrySet()) {
                byte[] k = keySer.serialize(e.getKey());
                byte[][] v = e.getValue().stream()
                        .map(valSer::serialize)
                        .toArray(byte[][]::new);
                conn.keyCommands().del(k);
                conn.setCommands().sAdd(k, v);
                conn.keyCommands().expire(k, ttlSeconds);
            }
            return null;
        });

        return total;

    }
}
