package com.dft.mom.domain.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class TwoLevelCacheManager implements CacheManager {

    private final CacheManager redisManager;
    private final CacheManager caffeineManager;

    @Override
    public Cache getCache(String name) {
        Cache redis    = redisManager.getCache(name);
        Cache caffeine = caffeineManager.getCache(name);
        return new TwoLevelCache(redis, caffeine);
    }

    @Override
    public Collection<String> getCacheNames() {
        Set<String> names = new HashSet<>();
        names.addAll(redisManager.getCacheNames());
        names.addAll(caffeineManager.getCacheNames());
        return names;
    }
}