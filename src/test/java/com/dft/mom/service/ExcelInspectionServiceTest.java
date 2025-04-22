package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.excel.ExcelInspectionService;
import com.dft.mom.web.exception.CommonException;
import com.dft.mom.web.exception.post.PageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.dft.mom.web.exception.ExceptionType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class ExcelInspectionServiceTest extends ServiceTest {

    @Autowired
    private ExcelInspectionService excelService;

    @BeforeEach
    public void setUp() {}

    @Test
    @DisplayName("1. INSPECTION 생성 - 엣지 케이스 - 1. INSPECTION ID 없음")
    public void INSPECTION_ID_없음() {
        String route = "validate/inspection/invalid/inspection_invalid_id_null.xlsx";

        CommonException exception1 = assertThrows(CommonException.class, () -> {
            excelService.createInspection(route);
        });

        assertEquals(ID_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. INSPECTION 생성 - 엣지 케이스 - 2. INSPECTION TITLE 없음")
    public void INSPECTION_TITLE_없음() {
        String route = "validate/inspection/invalid/inspection_invalid_title_null.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createInspection(route);
        });

        assertEquals(PAGE_TITLE_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. INSPECTION 생성 - 엣지 케이스 - 3. INSPECTION TITLE 초과")
    public void INSPECTION_TITLE_초과() {
        String route = "validate/inspection/invalid/inspection_invalid_title_exceed.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createInspection(route);
        });

        assertEquals(PAGE_TITLE_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. INSPECTION 생성 - 엣지 케이스 - 4. INSPECTION TIME 없음")
    public void INSPECTION_TIME_없음() {
        String route = "validate/inspection/invalid/inspection_invalid_time_null.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createInspection(route);
        });

        assertEquals(PAGE_TIME_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. INSPECTION 생성 - 엣지 케이스 - 5. INSPECTION TIME 미만")
    public void INSPECTION_TIME_미만() {
        String route = "validate/inspection/invalid/inspection_invalid_time_low.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createInspection(route);
        });

        assertEquals(PAGE_TIME_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. INSPECTION 생성 - 엣지 케이스 - 4. INSPECTION TIME 초과")
    public void INSPECTION_TIME_초과() {
        String route = "validate/inspection/invalid/inspection_invalid_time_exceed.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createInspection(route);
        });

        assertEquals(PAGE_TIME_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. INSPECTION 생성 - 엣지 케이스 - 4. INSPECTION TIME 역방향, 즉 start가 end보다 큰 경우")
    public void INSPECTION_TIME_역방향() {
        String route = "validate/inspection/invalid/inspection_invalid_time_inverse.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelService.createInspection(route);
        });

        assertEquals(PAGE_TIME_INVALID.getCode(), exception1.getCode());
    }
}