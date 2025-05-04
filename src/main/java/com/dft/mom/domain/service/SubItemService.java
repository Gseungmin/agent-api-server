package com.dft.mom.domain.service;

import com.dft.mom.domain.dto.item.res.ItemResponseDto;
import com.dft.mom.domain.dto.item.res.SubItemResponseDto;
import com.dft.mom.domain.entity.post.*;
import com.dft.mom.domain.repository.SubItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.dft.mom.domain.util.CommonConstants.SUB_ITEM_CACHE_KEY;
import static com.dft.mom.domain.util.PostConstants.*;

@Service
@RequiredArgsConstructor
@Transactional
public class SubItemService {

    private final SubItemRepository subItemRepository;

    /* 서브 아이템 캐시를 통해 조회 성능 개선 */
    @Transactional(readOnly = true)
    @Cacheable(
            value = SUB_ITEM_CACHE_KEY,
            key = "'cached-sub-item-' + #type + '-' + #itemId",
            sync = true
    )
    public ItemResponseDto getCachedItem(Integer type, Long itemId) {
        return getItem(type, itemId);
    }

    @Transactional(readOnly = true)
    @CachePut(
            value = SUB_ITEM_CACHE_KEY,
            key = "'cached-sub-item-' + #type + '-' + #itemId"
    )
    public ItemResponseDto putCachedItem(Integer type, Long itemId) {
        return getItem(type, itemId);
    }

    private ItemResponseDto getItem(Integer type, Long itemId) {
        if (type == TYPE_PREGNANCY_GUIDE || type == TYPE_CHILDCARE_GUIDE) {
            return getPostItem(itemId);
        }
        if (type == TYPE_PREGNANCY_NUTRITION || type == TYPE_CHILDCARE_NUTRITION) {
            return getNutritionItem(itemId);
        }

        return getInspectionItem(itemId);
    }

    /* Post 아이템 조회 */
    private ItemResponseDto getPostItem(Long itemId) {
        System.out.println("START-GET-ITEM");
        List<SubItem> subItemList = subItemRepository.findSubItemListByPostId(itemId);
        if (subItemList.isEmpty()) {
            System.out.println("PRE-END-ITEM");
            return null;
        }

        List<SubItemResponseDto> dtoList = subItemList.stream()
                .map(SubItemResponseDto::new)
                .toList();

        Post post = subItemList.get(0).getPost();
        System.out.println("END-ITEM");
        return new ItemResponseDto(post, dtoList);
    }

    /* Nutrition 아이템 조회 */
    private ItemResponseDto getNutritionItem(Long itemId) {
        List<SubItem> subItemList = subItemRepository.findSubItemListByNutritionId(itemId);
        if (subItemList.isEmpty()) {
            return null;
        }

        List<SubItemResponseDto> dtoList = subItemList.stream()
                .map(SubItemResponseDto::new)
                .toList();

        Nutrition nutrition = subItemList.get(0).getNutrition();
        return new ItemResponseDto(nutrition, dtoList);
    }

    /* Inspection 아이템 조회 */
    private ItemResponseDto getInspectionItem(Long itemId) {
        List<SubItem> subItemList = subItemRepository.findSubItemListByInspectionId(itemId);
        if (subItemList.isEmpty()) {
            return null;
        }

        List<SubItemResponseDto> dtoList = subItemList.stream()
                .map(SubItemResponseDto::new)
                .toList();

        Inspection inspection = subItemList.get(0).getInspection();
        return new ItemResponseDto(inspection, dtoList);
    }
}
