package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.excel.ExcelNutritionService;
import com.dft.mom.web.exception.CommonException;
import com.dft.mom.web.exception.post.PageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.dft.mom.domain.util.PostConstants.TYPE_PREGNANCY_NUTRITION;
import static com.dft.mom.web.exception.ExceptionType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class ExcelNutritionServiceTest extends ServiceTest {

    @Autowired
    private ExcelNutritionService excelService;

    @BeforeEach
    public void setUp() {}

    @Test
    @DisplayName("1. NUTRITION 생성 - 엣지 케이스 - 1. NUTRITION ID 없음")
    public void NUTRITION_ID_없음() {
        String route = "validate/nutrition/invalid/nutrition_invalid_id_null.xlsx";

        CommonException exception1 = assertThrows(CommonException.class, () -> {
            excelService.createNutrition(route, TYPE_PREGNANCY_NUTRITION);
        });

        assertEquals(ID_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. NUTRITION 생성 - 엣지 케이스 - 2. NUTRITION TITLE 없음")
    public void NUTRITION_TITLE_없음() {
        String route = "validate/nutrition/invalid/nutrition_invalid_title_null.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createNutrition(route, TYPE_PREGNANCY_NUTRITION);
        });

        assertEquals(PAGE_TITLE_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. NUTRITION 생성 - 엣지 케이스 - 3. NUTRITION TITLE 초과")
    public void NUTRITION_TITLE_초과() {
        String route = "validate/nutrition/invalid/nutrition_invalid_title_exceed.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createNutrition(route, TYPE_PREGNANCY_NUTRITION);
        });

        assertEquals(PAGE_TITLE_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. NUTRITION 생성 - 엣지 케이스 - 4. NUTRITION TAG 잘못됨")
    public void NUTRITION_TYPE_적음() {
        String route = "validate/nutrition/invalid/nutrition_invalid_tag_invalid.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createNutrition(route, TYPE_PREGNANCY_NUTRITION);
        });

        assertEquals(PAGE_TAG_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. NUTRITION 생성 - 엣지 케이스 - 5. NUTRITION CATEGORY 잘못됨")
    public void NUTRITION_TYPE_초과() {
        String route = "validate/nutrition/invalid/nutrition_invalid_category_invalid.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createNutrition(route, TYPE_PREGNANCY_NUTRITION);
        });

        assertEquals(PAGE_CATEGORY_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. NUTRITION 생성 - 엣지 케이스 - 6. NUTRITION SUMMARY 없음")
    public void NUTRITION_SUMMARY_없음() {
        String route = "validate/nutrition/invalid/nutrition_invalid_summary_null.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createNutrition(route, TYPE_PREGNANCY_NUTRITION);
        });

        assertEquals(PAGE_SUMMARY_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. NUTRITION 생성 - 엣지 케이스 - 7. NUTRITION SUMMARY 초과")
    public void NUTRITION_SUMMARY_초과() {
        String route = "validate/nutrition/invalid/nutrition_invalid_summary_exceed.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createNutrition(route, TYPE_PREGNANCY_NUTRITION);
        });

        assertEquals(PAGE_SUMMARY_INVALID.getCode(), exception1.getCode());
    }
}