package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.page.res.CategoryResponseDto;
import com.dft.mom.domain.dto.page.res.PageResponseDto;
import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.excel.ExcelInspectionService;
import com.dft.mom.domain.excel.ExcelNutritionService;
import com.dft.mom.domain.excel.ExcelPostService;
import com.dft.mom.domain.repository.PageRepository;
import com.dft.mom.domain.service.CacheUpdateService;
import com.dft.mom.domain.service.PageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static com.dft.mom.domain.util.PostConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class PageServiceTest extends ServiceTest {

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private PageService pageService;

    @Autowired
    private CacheUpdateService cacheUpdateService;

    @Autowired
    private ExcelPostService excelPostService;

    @Autowired
    private ExcelNutritionService excelNutritionService;

    @Autowired
    private ExcelInspectionService excelInspectionService;

    @BeforeEach
    public void setUp() throws IOException {
        String route = "validate/post/success/post_valid.xlsx";
        excelPostService.createPost(route);
        flushAndClear();

        String nutritionRoute = "validate/nutrition/success/nutrition_valid.xlsx";
        excelNutritionService.createNutrition(nutritionRoute, TYPE_PREGNANCY_NUTRITION);
        flushAndClear();

        String inspectionRoute = "validate/inspection/success/inspection_valid.xlsx";
        excelInspectionService.createInspection(inspectionRoute);
        flushAndClear();
    }

    @Test
    @DisplayName("1. 페이지 생성 - 해피 케이스 - 1. 페이지가 추가되어있다.")
    public void 페이지_생성() {
        //given when
        List<BabyPage> 페이지_전체조회 = pageRepository.findAll();

        //then
        assertThat(페이지_전체조회.size()).isEqualTo(TOTAL_PAGE_SIZE);
    }

    @Test
    @DisplayName("2. 페이지 조회 - 해피 케이스 - 1. 페이지를 조회할 수 있다.")
    public void 페이지_조회() {
        //given when
        PageResponseDto 페이지_조회 = pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        List<CategoryResponseDto> 카테고리리스트 = 페이지_조회.getCategoryList();
        CategoryResponseDto 카테고리1000 = 카테고리리스트.get(0);
        CategoryResponseDto 카테고리1003 = 카테고리리스트.get(1);

        //then
        assertThat(페이지_조회.getPageType()).isEqualTo(TYPE_PREGNANCY_GUIDE);
        assertThat(페이지_조회.getPagePeriod()).isEqualTo(FETAL_PERIOD_5_8);
        assertThat(카테고리리스트.size()).isEqualTo(2);

        assertThat(카테고리1000.getCategory()).isEqualTo(1000);
        assertThat(카테고리1003.getCategory()).isEqualTo(1003);

        assertThat(카테고리1000.getPostList().size()).isEqualTo(2);
        assertThat(카테고리1003.getPostList().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("2. 페이지 조회 - 해피 케이스 - 2. 페이지는 캐시 되어 있다.")
    public void 페이지_캐시_확인() {
        //given when
        pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        Boolean isCached1 = cacheUpdateService.validateCache(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        Boolean isCached2 = cacheUpdateService.validateCache(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_0_4);

        //then
        assertThat(isCached1).isEqualTo(true);
        assertThat(isCached2).isEqualTo(false);
    }

    @Test
    @DisplayName("2. 페이지 조회 - 해피 케이스 - 3. 캐시는 삭제 할 수 있다.")
    public void 페이지_캐시_삭제() {
        //given when
        pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        cacheUpdateService.deleteCache(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        Boolean isCached = cacheUpdateService.validateCache(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);

        //then
        assertThat(isCached).isEqualTo(false);
    }

    @Test
    @DisplayName("2. 페이지 조회 - 해피 케이스 - 4. 영양 페이지를 조회할 수 있다.")
    public void 영양_페이지_조회() {
        //given when
        PageResponseDto 페이지_조회 = pageService.getCachedPage(TYPE_PREGNANCY_NUTRITION, PERIOD_TOTAL);
        List<CategoryResponseDto> 카테고리리스트 = 페이지_조회.getCategoryList();
        CategoryResponseDto 카테고리2000 = 카테고리리스트.get(0);

        //then
        assertThat(페이지_조회.getPageType()).isEqualTo(TYPE_PREGNANCY_NUTRITION);
        assertThat(페이지_조회.getPagePeriod()).isEqualTo(PERIOD_TOTAL);
        assertThat(카테고리리스트.size()).isEqualTo(1);
        assertThat(카테고리2000.getCategory()).isEqualTo(2000);
        assertThat(카테고리2000.getNutritionList().size()).isEqualTo(8);
    }

    @Test
    @DisplayName("2. 페이지 조회 - 해피 케이스 - 5. 검사 페이지를 조회할 수 있다.")
    public void 검사_페이지_조회() {
        //given when
        PageResponseDto 페이지_조회 = pageService.getCachedPage(TYPE_INSPECTION, PERIOD_TOTAL);
        List<CategoryResponseDto> 카테고리리스트 = 페이지_조회.getCategoryList();
        CategoryResponseDto 카테고리3000 = 카테고리리스트.get(0);

        //then
        assertThat(페이지_조회.getPageType()).isEqualTo(TYPE_INSPECTION);
        assertThat(페이지_조회.getPagePeriod()).isEqualTo(PERIOD_TOTAL);
        assertThat(카테고리리스트.size()).isEqualTo(1);
        assertThat(카테고리3000.getCategory()).isEqualTo(3000);
        assertThat(카테고리3000.getInspectionList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("3. 페이지 캐시 업데이트 - 해피 케이스 - 1. 페이지를 업데이트 할 수 있다.")
    public void 페이지_업데이트() throws IOException {
        //given
        pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        flushAndClear();

        String route = "validate/post/success/post_valid_add.xlsx";
        excelPostService.createPost(route);
        flushAndClear();

        //when
        PageResponseDto 페이지_조회 = pageService.putCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        List<CategoryResponseDto> 카테고리리스트 = 페이지_조회.getCategoryList();
        CategoryResponseDto 카테고리1000 = 카테고리리스트.get(0);
        CategoryResponseDto 카테고리1003 = 카테고리리스트.get(1);

        //then
        assertThat(페이지_조회.getPageType()).isEqualTo(TYPE_PREGNANCY_GUIDE);
        assertThat(페이지_조회.getPagePeriod()).isEqualTo(FETAL_PERIOD_5_8);
        assertThat(카테고리리스트.size()).isEqualTo(2);

        assertThat(카테고리1000.getCategory()).isEqualTo(1000);
        assertThat(카테고리1003.getCategory()).isEqualTo(1003);

        assertThat(카테고리1000.getPostList().size()).isEqualTo(1);
        assertThat(카테고리1003.getPostList().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("3. 페이지 캐시 업데이트 - 해피 케이스 - 2. 페이지를 업데이트 후 캐시 확인")
    public void 페이지_업데이트_캐시_확인() throws IOException {
        //given
        pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        flushAndClear();

        String route = "validate/post/success/post_valid_add.xlsx";
        excelPostService.createPost(route);
        flushAndClear();

        //when
        Boolean isCached1 = cacheUpdateService.validateCache(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        Boolean isCached2 = cacheUpdateService.validateCache(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_0_4);

        //then
        assertThat(isCached1).isEqualTo(true);
        assertThat(isCached2).isEqualTo(false);
    }

    @Test
    @DisplayName("3. 페이지 캐시 업데이트 - 해피 케이스 - 3. 페이지를 업데이트 후 캐시 삭제 확인")
    public void 페이지_업데이트_캐시_삭제() throws IOException {
        //given
        pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        flushAndClear();

        String route = "validate/post/success/post_valid_add.xlsx";
        excelPostService.createPost(route);
        flushAndClear();

        //when
        pageService.putCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        cacheUpdateService.deleteCache(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        Boolean isCached = cacheUpdateService.validateCache(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);

        //then
        assertThat(isCached).isEqualTo(false);
    }

    @Test
    @DisplayName("3. 페이지 캐시 업데이트 - 해피 케이스 - 4. 영양 페이지를 업데이트 할 수 있다.")
    public void 영양_페이지_업데이트() throws IOException {
        //given
        pageService.getCachedPage(TYPE_PREGNANCY_NUTRITION, PERIOD_TOTAL);
        flushAndClear();

        String route = "validate/nutrition/success/nutrition_valid_add.xlsx";
        excelNutritionService.createNutrition(route, TYPE_PREGNANCY_NUTRITION);
        flushAndClear();

        //when
        PageResponseDto 페이지_조회 = pageService.putCachedPage(TYPE_PREGNANCY_NUTRITION, PERIOD_TOTAL);
        List<CategoryResponseDto> 카테고리리스트 = 페이지_조회.getCategoryList();
        CategoryResponseDto 카테고리2000 = 카테고리리스트.get(0);
        CategoryResponseDto 카테고리2002 = 카테고리리스트.get(1);

        //then
        assertThat(페이지_조회.getPageType()).isEqualTo(TYPE_PREGNANCY_NUTRITION);
        assertThat(페이지_조회.getPagePeriod()).isEqualTo(PERIOD_TOTAL);
        assertThat(카테고리리스트.size()).isEqualTo(2);

        assertThat(카테고리2000.getCategory()).isEqualTo(2000);
        assertThat(카테고리2002.getCategory()).isEqualTo(2002);

        assertThat(카테고리2000.getNutritionList().size()).isEqualTo(8);
        assertThat(카테고리2002.getNutritionList().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("3. 페이지 캐시 업데이트 - 해피 케이스 - 4. 검사 페이지를 업데이트 할 수 있다.")
    public void 검사_페이지_업데이트() throws IOException {
        //given
        pageService.getCachedPage(TYPE_INSPECTION, PERIOD_TOTAL);
        flushAndClear();

        String route = "validate/inspection/success/inspection_valid_add.xlsx";
        excelInspectionService.createInspection(route);
        flushAndClear();

        //when
        PageResponseDto 페이지_조회 = pageService.putCachedPage(TYPE_INSPECTION, PERIOD_TOTAL);
        List<CategoryResponseDto> 카테고리리스트 = 페이지_조회.getCategoryList();
        CategoryResponseDto 카테고리3000 = 카테고리리스트.get(0);

        //then
        assertThat(페이지_조회.getPageType()).isEqualTo(TYPE_INSPECTION);
        assertThat(페이지_조회.getPagePeriod()).isEqualTo(PERIOD_TOTAL);
        assertThat(카테고리리스트.size()).isEqualTo(1);

        assertThat(카테고리3000.getCategory()).isEqualTo(3000);
        assertThat(카테고리3000.getInspectionList().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("4. 페이지 버전 체크 - 해피 케이스 - 1. 페이지를 업데이트 하면 버전이 수정된다")
    public void 페이지_버전_체크() throws IOException {
        //given
        PageResponseDto 페이지V1 = pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        assertThat(페이지V1.getVersion()).isEqualTo(0);
        flushAndClear();

        //when
        String route = "validate/post/success/post_valid_add.xlsx";
        excelPostService.createPost(route);
        flushAndClear();

        //when
        cacheUpdateService.updateCachedPage();
        flushAndClear();

        PageResponseDto 페이지V2 = pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);
        assertThat(페이지V2.getVersion()).isEqualTo(1);
    }
}