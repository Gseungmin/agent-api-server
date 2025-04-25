package com.dft.mom.domain.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TwoLevelCache implements Cache {

    private final Cache redis;
    private final Cache local;

    @Override
    public String getName() {
        return redis.getName();
    }

    @Override
    public Object getNativeCache() {
        return redis.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        try {
            return redis.get(key);
        } catch (RedisConnectionFailureException | RedisSystemException ex) {
            return local.get(key);
        } catch (RuntimeException ex) {
            if (isRedisConnectionFailure(ex)) {
                return local.get(key);
            }
            throw ex;
        }
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        try {
            return redis.get(key, type);
        } catch (RedisConnectionFailureException | RedisSystemException ex) {
            return local.get(key, type);
        } catch (RuntimeException ex) {
            if (isRedisConnectionFailure(ex)) {
                return local.get(key, type);
            }
            throw ex;
        }
    }

    @Override
    public <T> T get(Object key, Callable<T> loader) {
        try {
            return redis.get(key, loader);
        } catch (RedisConnectionFailureException | RedisSystemException ex) {
            return local.get(key, loader);
        } catch (RuntimeException ex) {
            if (isRedisConnectionFailure(ex)) {
                return local.get(key, loader);
            }
            throw ex;
        }
    }

    @Override
    public void put(Object key, Object value) {
        executeSafely(
                () -> redis.put(key, value),
                () -> local.put(key, value)
        );
    }

    @Override
    public void evict(Object key) {
        executeSafely(
                () -> redis.evict(key),
                () -> local.evict(key)
        );
    }

    @Override
    public void clear() {
        executeSafely(
                redis::clear,
                local::clear
        );
    }

    private boolean isRedisConnectionFailure(RuntimeException ex) {
        Throwable cause = ex.getCause();
        return cause instanceof RedisConnectionFailureException
                || cause instanceof RedisSystemException;
    }

    private void executeSafely(Runnable primary, Runnable fallback) {
        try {
            primary.run();
        } catch (RedisConnectionFailureException | RedisSystemException ex) {
            fallback.run();
        } catch (RuntimeException ex) {
            if (isRedisConnectionFailure(ex)) {
                fallback.run();
                return;
            }
            throw ex;
        }
    }
}
