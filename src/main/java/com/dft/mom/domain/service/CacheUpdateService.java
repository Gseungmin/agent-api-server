package com.dft.mom.domain.service;

import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.repository.PageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CacheUpdateService {

    private final PageService pageService;
    private final PageRepository pageRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void updateCachedItem() {
        pageRepository.incrementAllVersions();
        List<BabyPage> updatedPageList = pageService.getPageList();
        for (BabyPage page : updatedPageList) {
            pageService.putCachedPage(page.getType(), page.getPeriod());
        }
    }

    /*캐시 체크*/
    public Boolean validateCache(Integer type, Integer period) {
        String key = "pageCache::cached-page-" + type + "-" + period;
        Object cachedValue = redisTemplate.opsForValue().get(key);
        return cachedValue != null;
    }

    /*캐시 무효화*/
    public Boolean deleteCache(Integer type, Integer period) {
        String key = "pageCache::cached-page-" + type + "-" + period;
        return redisTemplate.delete(key);
    }
}