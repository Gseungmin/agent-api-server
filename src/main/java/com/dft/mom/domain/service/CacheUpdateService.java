package com.dft.mom.domain.service;

import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.entity.post.Inspection;
import com.dft.mom.domain.entity.post.Nutrition;
import com.dft.mom.domain.entity.post.Post;
import com.dft.mom.domain.repository.InspectionRepository;
import com.dft.mom.domain.repository.NutritionRepository;
import com.dft.mom.domain.repository.PageRepository;
import com.dft.mom.domain.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dft.mom.domain.util.PostConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CacheUpdateService {

    private final PageService pageService;
    private final PageRepository pageRepository;
    private final PostRepository postRepository;
    private final NutritionRepository nutritionRepository;
    private final InspectionRepository inspectionRepository;
    private final SubItemService itemService;
    private final RedisTemplate<String, Object> redisTemplate;

    public void updateCachedPage() {
        pageRepository.incrementAllVersions();

        List<BabyPage> updatedPageList = pageService.getPageList();
        for (BabyPage page : updatedPageList) {
            pageService.putCachedPage(page.getType(), page.getPeriod());
        }
    }

    public void updateCachedPost() {
        postRepository.incrementAllVersions();

        List<Post> postList = postRepository.findAll();
        for (Post item : postList) {
            itemService.putCachedItem(TYPE_PREGNANCY_GUIDE, item.getItemId());
        }
    }

    public void updateCachedNutrition() {
        nutritionRepository.incrementAllVersions();

        List<Nutrition> nutritionList = nutritionRepository.findAll();
        for (Nutrition item : nutritionList) {
            itemService.putCachedItem(TYPE_PREGNANCY_NUTRITION, item.getItemId());
        }
    }

    public void updateCachedInspection() {
        inspectionRepository.incrementAllVersions();

        List<Inspection> inspectionList = inspectionRepository.findAll();
        for (Inspection item : inspectionList) {
            itemService.putCachedItem(TYPE_INSPECTION, item.getItemId());
        }
    }

    /* 캐시 체크 */
    public Boolean validateCache(Integer type, Integer period) {
        String key = "pageCache::cached-page-" + type + "-" + period;
        Object cachedValue = redisTemplate.opsForValue().get(key);
        return cachedValue != null;
    }

    /* 캐시 무효화 */
    public Boolean deleteCache(Integer type, Integer period) {
        String key = "pageCache::cached-page-" + type + "-" + period;
        return redisTemplate.delete(key);
    }

    /* subItemCache 체크 */
    public Boolean validateSubItemCache(Integer type, Long itemId) {
        String key = "subItemCache::cached-sub-item-" + type + "-" + itemId;
        Object cachedValue = redisTemplate.opsForValue().get(key);
        return cachedValue != null;
    }

    /* subItemCache 무효화 */
    public Boolean deleteSubItemCache(Integer type, Long itemId) {
        String key = "subItemCache::cached-sub-item-" + type + "-" + itemId;
        return redisTemplate.delete(key);
    }
}
