package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.excel.ExcelPostService;
import com.dft.mom.domain.repository.PostRepository;
import com.dft.mom.domain.service.PageService;
import com.dft.mom.web.exception.CommonException;
import com.dft.mom.web.exception.post.PageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static com.dft.mom.web.exception.ExceptionType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class ExcelPostServiceTest extends ServiceTest {

    @Autowired
    private ExcelPostService excelPostService;

    @BeforeEach
    public void setUp() {}

    @Test
    @DisplayName("0. POST 생성 - 해피 케이스 - 1. POST 저장된다.")
    public void POST_저장() throws IOException {
        String route = "validate/post/post_valid.xlsx";
        excelPostService.createPost(route);
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 1. POST ID 없음")
    public void POST_ID_없음() {
        String route = "validate/post/post_invalid_id_null.xlsx";

        CommonException exception1 = assertThrows(CommonException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(ID_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 2. POST TITLE 없음")
    public void POST_TITLE_없음() {
        String route = "validate/post/post_invalid_title_null.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(PAGE_TITLE_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 3. POST TITLE 초과")
    public void POST_TITLE_초과() {
        String route = "validate/post/post_invalid_title_exceed.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(PAGE_TITLE_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 4. POST TYPE 적음")
    public void POST_TYPE_적음() {
        String route = "validate/post/post_invalid_type_low.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(PAGE_TYPE_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 5. POST TYPE 초과")
    public void POST_TYPE_초과() {
        String route = "validate/post/post_invalid_type_exceed.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(PAGE_TYPE_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 6. POST PERIOD 0 적음")
    public void POST_PERIOD_임신중_적음() {
        String route = "validate/post/post_invalid_period_0_low.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(PAGE_PERIOD_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 7. POST PERIOD 0 초과")
    public void POST_PERIOD_임신중_초과() {
        String route = "validate/post/post_invalid_period_0_exceed.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(PAGE_PERIOD_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 8. POST PERIOD 3 적음")
    public void POST_PERIOD_육아중_적음() {
        String route = "validate/post/post_invalid_period_3_low.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(PAGE_PERIOD_INVALID.getCode(), exception1.getCode());
    }

    @Test
    @DisplayName("1. POST 생성 - 엣지 케이스 - 9. POST PERIOD 3 초과")
    public void POST_PERIOD_육아중_초과() {
        String route = "validate/post/post_invalid_period_3_exceed.xlsx";

        PageException exception1 = assertThrows(PageException.class, () -> {
            excelPostService.createPost(route);
        });

        assertEquals(PAGE_PERIOD_INVALID.getCode(), exception1.getCode());
    }
}