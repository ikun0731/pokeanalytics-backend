package com.pokeanalytics.pokestatsservice.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 * 配置RedisTemplate和缓存管理器
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置自定义RedisTemplate
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisTemplate实例
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用StringRedisSerializer序列化key
        template.setKeySerializer(new StringRedisSerializer());
        // 使用GenericJackson2JsonRedisSerializer序列化value
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        
        // Hash的key和value也使用相同的序列化
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置Redis缓存管理器
     * 设置不同缓存名称的过期时间
     *
     * @param connectionFactory Redis连接工厂
     * @return RedisCacheManager实例
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // 默认配置
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                // 设置key序列化器
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 设置value序列化器
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                // 设置默认过期时间
                .entryTtl(Duration.ofHours(1));
        
        // 不同缓存名称的特定配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        // leaderboards缓存有效期为30分钟
        configMap.put("leaderboards", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));
        // stats_details缓存有效期为1小时
        configMap.put("stats_details", defaultCacheConfig.entryTtl(Duration.ofHours(1)));
        
        // 创建并返回Redis缓存管理器
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(configMap)
                .build();
    }
}
