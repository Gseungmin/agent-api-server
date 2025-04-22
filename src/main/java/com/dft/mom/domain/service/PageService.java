package com.dft.mom.domain.service;

import com.dft.mom.domain.dto.page.res.*;
import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.entity.post.BabyPageItem;
import com.dft.mom.domain.repository.PageItemRepository;
import com.dft.mom.domain.repository.PageRepository;
import com.dft.mom.web.exception.post.PageException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.dft.mom.domain.util.PostConstants.*;
import static com.dft.mom.web.exception.ExceptionType.PAGE_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional
public class PageService {

    private final PageRepository pageRepository;
    private final PageItemRepository pageItemRepository;

    /*페이지 캐시를 통해 조회 성능 개선*/
    @Transactional(readOnly = true)
    @Cacheable(value = "pageCache", key = "'cached-page-' + #type + '-' + #period")
    public PageResponseDto getCachedPage(Integer type, Integer period) {
        BabyPage babyPage = getPage(type, period);

        if (babyPage.getType() == TYPE_PREGNANCY_GUIDE || babyPage.getType() == TYPE_CHILDCARE_GUIDE) {
            List<CategoryResponseDto> categoryList = getPageItemWithPost(babyPage);
            return new PageResponseDto(babyPage, categoryList);
        }

        if (babyPage.getType() == TYPE_CHILDCARE_NUTRITION || babyPage.getType() == TYPE_PREGNANCY_NUTRITION) {
            List<CategoryResponseDto> categoryList = getPageItemWithNutrition(babyPage);
            return new PageResponseDto(babyPage, categoryList);
        }

        List<CategoryResponseDto> categoryList = getPageItemWithInspection(babyPage);
        return new PageResponseDto(babyPage, categoryList);
    }

    /*페이지 업데이트를 통해 캐시 미스 개선*/
    @Transactional(readOnly = true)
    @CachePut(value = "pageCache", key = "'cached-page-' + #type + '-' + #period")
    public PageResponseDto putCachedPage(Integer type, Integer period) {
        BabyPage babyPage = getPage(type, period);

        if (babyPage.getType() == TYPE_PREGNANCY_GUIDE || babyPage.getType() == TYPE_CHILDCARE_GUIDE) {
            List<CategoryResponseDto> categoryList = getPageItemWithPost(babyPage);
            return new PageResponseDto(babyPage, categoryList);
        }

        if (babyPage.getType() == TYPE_CHILDCARE_NUTRITION || babyPage.getType() == TYPE_PREGNANCY_NUTRITION) {
            List<CategoryResponseDto> categoryList = getPageItemWithNutrition(babyPage);
            return new PageResponseDto(babyPage, categoryList);
        }

        List<CategoryResponseDto> categoryList = getPageItemWithInspection(babyPage);
        return new PageResponseDto(babyPage, categoryList);
    }

    /*페이지 조회*/
    @Transactional(readOnly = true)
    public BabyPage getPage(Integer type, Integer period) {
        Optional<BabyPage> optPage = pageRepository.findBabyByTypeAndPeriod(type, period);

        if (optPage.isEmpty()) {
            throw new PageException(PAGE_NOT_EXIST.getCode(), PAGE_NOT_EXIST.getErrorMessage());
        }

        return optPage.get();
    }

    /*
     * 페이지 응답 생성
     * 페이지와 연관관계를 가지는 BabyPageItem을 리스트로 조회
     * 페치 조인을 통해 관련 POST 같이 조회
     * */
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getPageItemWithPost(BabyPage babyPage) {
        List<BabyPageItem> pageItemList = pageItemRepository.findBabyPageItemWithPost(babyPage);

        List<PostResponseDto> itemList = pageItemList.stream()
                .map(item -> new PostResponseDto(item.getPost())).toList();

        Map<Integer, List<PostResponseDto>> groupedByCategory = itemList.stream()
                .collect(Collectors.groupingBy(PostResponseDto::getCategory));

        return groupedByCategory.entrySet().stream().map(entry ->
                new CategoryResponseDto(entry.getKey(), entry.getValue(), null, null)).toList();
    }

    /*
     * 페이지 응답 생성
     * 페이지와 연관관계를 가지는 BabyPageItem을 리스트로 조회
     * 페치 조인을 통해 관련 Nutrition 같이 조회
     * */
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getPageItemWithNutrition(BabyPage babyPage) {
        List<BabyPageItem> pageItemList = pageItemRepository.findBabyPageItemWithNutrition(babyPage);

        List<NutritionResponseDto> itemList = pageItemList.stream()
                .map(item -> new NutritionResponseDto(item.getNutrition())).toList();

        Map<Integer, List<NutritionResponseDto>> groupedByCategory = itemList.stream()
                .collect(Collectors.groupingBy(NutritionResponseDto::getCategory));

        return groupedByCategory.entrySet().stream().map(entry ->
                new CategoryResponseDto(entry.getKey(), null, entry.getValue(), null)).toList();
    }

    /*
     * 페이지 응답 생성
     * 페이지와 연관관계를 가지는 BabyPageItem을 리스트로 조회
     * 페치 조인을 통해 관련 INSPECTION 같이 조회
     * */
    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getPageItemWithInspection(BabyPage babyPage) {
        List<BabyPageItem> inspectionItemList = pageItemRepository.findBabyPageItemWithInspection(babyPage);

        List<InspectionResponseDto> itemList = inspectionItemList.stream()
                .map(item -> new InspectionResponseDto(item.getInspection())).toList();

        Map<Integer, List<InspectionResponseDto>> groupedByCategory = itemList.stream()
                .collect(Collectors.groupingBy(InspectionResponseDto::getCategory));

        return groupedByCategory.entrySet().stream().map(entry ->
                new CategoryResponseDto(entry.getKey(), null, null, entry.getValue())).toList();
    }

    @Transactional(readOnly = true)
    public List<BabyPage> getPageList() {
        return pageRepository.findAll();
    }

    @PostConstruct
    public void init() {
        initPages(TYPE_INSPECTION,   List.of(PERIOD_TOTAL));
        initPages(TYPE_PREGNANCY_GUIDE,  FETAL_PERIOD_LIST);
        initPages(TYPE_CHILDCARE_GUIDE,  BABY_PERIOD_LIST);
        initPages(TYPE_PREGNANCY_NUTRITION,  List.of(PERIOD_TOTAL));
        initPages(TYPE_CHILDCARE_NUTRITION,  List.of(PERIOD_TOTAL));
    }

    private void initPages(int type, List<Integer> periods) {
        for (Integer period : periods) {
            createPages(type, period);
        }
    }

    private void createPages(int type, int period) {
        Optional<BabyPage> page = pageRepository.findBabyByTypeAndPeriod(type, period);

        if (page.isEmpty()) {
            pageRepository.save(new BabyPage(type, period));
        }
    }
}