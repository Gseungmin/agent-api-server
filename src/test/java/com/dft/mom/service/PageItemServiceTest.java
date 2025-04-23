package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.item.res.ItemResponseDto;
import com.dft.mom.domain.dto.page.res.*;
import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.excel.ExcelInspectionService;
import com.dft.mom.domain.excel.ExcelNutritionService;
import com.dft.mom.domain.excel.ExcelPostService;
import com.dft.mom.domain.repository.PageRepository;
import com.dft.mom.domain.service.CacheUpdateService;
import com.dft.mom.domain.service.PageService;
import com.dft.mom.domain.service.SubItemService;
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
public class PageItemServiceTest extends ServiceTest {

    @Autowired
    private PageService pageService;

    @Autowired
    private SubItemService itemService;

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
    @DisplayName("1. 아이템 조회 - 해피 케이스 - 1. 포스트 페이지가 추가되면 아이템도 추가된다.")
    public void 포스트_아이템_조회() {
        //given when
        PageResponseDto 포스트페이지_조회 = pageService.getCachedPage(TYPE_PREGNANCY_GUIDE, FETAL_PERIOD_5_8);

        List<PostResponseDto> 카테고리1000포스트리스트 = 포스트페이지_조회.getCategoryList().get(0).getPostList();
        PostResponseDto 포스트1 = 카테고리1000포스트리스트.get(0);
        PostResponseDto 포스트2 = 카테고리1000포스트리스트.get(1);

        ItemResponseDto 아이템1 = itemService.getCachedItem(TYPE_PREGNANCY_GUIDE, 포스트1.getItemId());
        ItemResponseDto 아이템2 = itemService.getCachedItem(TYPE_PREGNANCY_GUIDE, 포스트2.getItemId());

        //then
        assertThat(아이템1.getId()).isEqualTo(포스트1.getItemId());
        assertThat(아이템1.getTitle()).isEqualTo(포스트1.getTitle());
        assertThat(아이템1.getContent()).isEqualTo(포스트1.getContent());
        assertThat(아이템1.getSubItemList().size()).isEqualTo(4);

        assertThat(아이템2.getId()).isEqualTo(포스트2.getItemId());
        assertThat(아이템2.getTitle()).isEqualTo(포스트2.getTitle());
        assertThat(아이템2.getContent()).isEqualTo(포스트2.getContent());
        assertThat(아이템2.getSubItemList().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("1. 아이템 생성 - 해피 케이스 - 2. 영양 페이지가 추가되면 아이템도 추가된다.")
    public void 영양_아이템_생성() {
        //given when
        PageResponseDto 영양페이지_조회 = pageService.getCachedPage(TYPE_PREGNANCY_NUTRITION, PERIOD_TOTAL);

        List<NutritionResponseDto> 영양리스트 = 영양페이지_조회.getCategoryList().get(0).getNutritionList();
        NutritionResponseDto 영양1 = 영양리스트.get(0);
        NutritionResponseDto 영양2 = 영양리스트.get(0);
        NutritionResponseDto 영양3 = 영양리스트.get(0);
        NutritionResponseDto 영양4 = 영양리스트.get(0);

        ItemResponseDto 아이템1 = itemService.getCachedItem(TYPE_CHILDCARE_NUTRITION, 영양1.getItemId());
        ItemResponseDto 아이템2 = itemService.getCachedItem(TYPE_CHILDCARE_NUTRITION, 영양2.getItemId());
        ItemResponseDto 아이템3 = itemService.getCachedItem(TYPE_CHILDCARE_NUTRITION, 영양3.getItemId());
        ItemResponseDto 아이템4 = itemService.getCachedItem(TYPE_CHILDCARE_NUTRITION, 영양4.getItemId());

        //then
        assertThat(아이템1.getId()).isEqualTo(영양1.getItemId());
        assertThat(아이템1.getTitle()).isEqualTo(영양1.getTitle());
        assertThat(아이템1.getContent()).isEqualTo(영양1.getContent());
        assertThat(아이템1.getSubItemList().size()).isEqualTo(5);

        assertThat(아이템2.getId()).isEqualTo(영양2.getItemId());
        assertThat(아이템2.getTitle()).isEqualTo(영양2.getTitle());
        assertThat(아이템2.getContent()).isEqualTo(영양2.getContent());
        assertThat(아이템2.getSubItemList().size()).isEqualTo(5);

        assertThat(아이템3.getId()).isEqualTo(영양3.getItemId());
        assertThat(아이템3.getTitle()).isEqualTo(영양3.getTitle());
        assertThat(아이템3.getContent()).isEqualTo(영양3.getContent());
        assertThat(아이템3.getSubItemList().size()).isEqualTo(5);

        assertThat(아이템4.getId()).isEqualTo(영양4.getItemId());
        assertThat(아이템4.getTitle()).isEqualTo(영양4.getTitle());
        assertThat(아이템4.getContent()).isEqualTo(영양4.getContent());
        assertThat(아이템4.getSubItemList().size()).isEqualTo(5);
    }

    @Test
    @DisplayName("1. 아이템 조회 - 해피 케이스 - 3. 검사 페이지가 추가되면 아이템도 추가된다.")
    public void 검사_아이템_조회() {
        //given when
        PageResponseDto 검사페이지_조회 = pageService.getCachedPage(TYPE_INSPECTION, PERIOD_TOTAL);

        List<InspectionResponseDto> 검사리스트 = 검사페이지_조회.getCategoryList().get(0).getInspectionList();
        InspectionResponseDto 검사1 = 검사리스트.get(0);

        ItemResponseDto 아이템1 = itemService.getCachedItem(TYPE_INSPECTION, 검사1.getItemId());

        //then
        assertThat(아이템1.getId()).isEqualTo(검사1.getItemId());
        assertThat(아이템1.getTitle()).isEqualTo(검사1.getTitle());
        assertThat(아이템1.getContent()).isEqualTo(검사1.getContent());
        assertThat(아이템1.getSubItemList().size()).isEqualTo(4);
    }

    @Test
    @DisplayName("1. 아이템 조회 - 엣지 케이스 - 4. 잘못된 아이템 조회시 NULL이 반환된다.")
    public void 잘못된아이템_조회() {
        //given when
        PageResponseDto 검사페이지_조회 = pageService.getCachedPage(TYPE_INSPECTION, PERIOD_TOTAL);

        List<InspectionResponseDto> 검사리스트 = 검사페이지_조회.getCategoryList().get(0).getInspectionList();
        InspectionResponseDto 검사1 = 검사리스트.get(0);

        ItemResponseDto 아이템1 = itemService.getCachedItem(TYPE_PREGNANCY_GUIDE, 검사1.getItemId());

        //then
        assertThat(아이템1).isNull();
    }

    @Test
    @DisplayName("2. 아이템 캐시 - 해피 케이스 - 1. 아이템은 캐시 되어 있다.")
    public void 아이템_캐시_확인() {
        //given when
        PageResponseDto 검사페이지_조회 = pageService.getCachedPage(TYPE_INSPECTION, PERIOD_TOTAL);
        List<InspectionResponseDto> 검사리스트 = 검사페이지_조회.getCategoryList().get(0).getInspectionList();
        InspectionResponseDto 검사1 = 검사리스트.get(0);
        ItemResponseDto 아이템1 = itemService.getCachedItem(TYPE_INSPECTION, 검사1.getItemId());

        Boolean isCached1 = cacheUpdateService.validateSubItemCache(TYPE_INSPECTION, 아이템1.getId());
        Boolean isCached2 = cacheUpdateService.validateSubItemCache(TYPE_INSPECTION, 1L);

        //then
        assertThat(isCached1).isEqualTo(true);
        assertThat(isCached2).isEqualTo(false);
    }

    @Test
    @DisplayName("2. 아이템 캐시 - 해피 케이스 - 2. 캐시는 삭제될 수 있다.")
    public void 아이템_캐시_삭제() {
        //given when
        PageResponseDto 검사페이지_조회 = pageService.getCachedPage(TYPE_INSPECTION, PERIOD_TOTAL);
        List<InspectionResponseDto> 검사리스트 = 검사페이지_조회.getCategoryList().get(0).getInspectionList();
        InspectionResponseDto 검사1 = 검사리스트.get(0);
        ItemResponseDto 아이템1 = itemService.getCachedItem(TYPE_INSPECTION, 검사1.getItemId());

        cacheUpdateService.deleteSubItemCache(TYPE_INSPECTION, 아이템1.getId());
        Boolean isCached = cacheUpdateService.validateSubItemCache(TYPE_INSPECTION, 아이템1.getId());

        //then
        assertThat(isCached).isEqualTo(false);
    }

    @Test
    @DisplayName("2. 아이템 캐시 - 해피 케이스 - 3. 캐시는 업데이트 될 수 있다.")
    public void 아이템_캐시_업데이트() throws IOException {
        //given
        PageResponseDto 검사페이지_조회 = pageService.getCachedPage(TYPE_INSPECTION, PERIOD_TOTAL);
        List<InspectionResponseDto> 검사리스트 = 검사페이지_조회.getCategoryList().get(0).getInspectionList();
        InspectionResponseDto 검사1 = 검사리스트.get(0);
        ItemResponseDto 아이템1 = itemService.getCachedItem(TYPE_INSPECTION, 검사1.getItemId());
        Long 기존서브아이템아이디 = 아이템1.getSubItemList().get(0).getItemId();
        String 기존서브아이템내용 = 아이템1.getSubItemList().get(0).getContent();
        flushAndClear();

        //when
        String inspectionRoute = "validate/inspection/success/inspection_valid_item_update.xlsx";
        excelInspectionService.createInspection(inspectionRoute);
        flushAndClear();

        cacheUpdateService.updateCachedInspection();
        flushAndClear();

        ItemResponseDto 아이템1_V2 = itemService.getCachedItem(TYPE_INSPECTION, 검사1.getItemId());
        Long 변화된서브아이템아이디 = 아이템1_V2.getSubItemList().get(0).getItemId();
        String 변화된서브아이템내용 = 아이템1_V2.getSubItemList().get(0).getContent();

        //then
        assertThat(기존서브아이템아이디).isEqualTo(변화된서브아이템아이디);
        assertThat(기존서브아이템내용).isNotEqualTo(변화된서브아이템내용);
    }
}