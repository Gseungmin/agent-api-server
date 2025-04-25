package com.dft.mom.web.config;

import com.dft.mom.domain.cache.TwoLevelCacheManager;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static com.dft.mom.domain.util.CommonConstants.PAGE_CACHE_KEY;
import static com.dft.mom.domain.util.CommonConstants.SUB_ITEM_CACHE_KEY;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory cf) {
        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofDays(7))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
        return RedisCacheManager.builder(cf).cacheDefaults(cacheConfiguration).build();
    }

    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager(
                PAGE_CACHE_KEY, SUB_ITEM_CACHE_KEY
        );

        manager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(60, TimeUnit.MINUTES));

        return manager;
    }

    @Bean
    @Primary
    CacheManager cacheManager(
            RedisCacheManager redisCacheManager,
            CaffeineCacheManager caffeineCacheManager
    ) {
        return new TwoLevelCacheManager(redisCacheManager, caffeineCacheManager);
    }

    @Bean
    public CacheErrorHandler gracefulErrorHandler() {
        return new SimpleCacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException ex, Cache cache, Object key) {
                if (ex instanceof RedisConnectionFailureException) return;
                throw ex;
            }

            @Override
            public void handleCachePutError(RuntimeException ex, Cache cache, Object key, Object value) {
                if (ex instanceof RedisConnectionFailureException) return;
                throw ex;
            }

            @Override
            public void handleCacheEvictError(RuntimeException ex, Cache cache, Object key) {
                if (ex instanceof RedisConnectionFailureException) return;
                throw ex;
            }

            @Override
            public void handleCacheClearError(RuntimeException ex, Cache cache) {
                if (ex instanceof RedisConnectionFailureException) return;
                throw ex;
            }
        };
    }
}