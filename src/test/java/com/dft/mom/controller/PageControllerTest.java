package com.dft.mom.controller;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberUpdateRequestDto;
import com.dft.mom.domain.dto.member.res.MemberResponseDto;
import com.dft.mom.domain.dto.member.res.MemberUpdateResponseDto;
import com.dft.mom.domain.dto.member.res.TokenResponseDto;
import com.dft.mom.domain.dto.page.res.PageResponseDto;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.excel.ExcelInspectionService;
import com.dft.mom.domain.excel.ExcelNutritionService;
import com.dft.mom.domain.excel.ExcelPostService;
import com.dft.mom.domain.service.CacheUpdateService;
import com.dft.mom.domain.service.LoginService;
import com.dft.mom.domain.service.MemberService;
import com.dft.mom.web.exception.ErrorResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.dft.mom.CreateUtil.*;
import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.domain.util.PostConstants.*;
import static com.dft.mom.web.exception.ExceptionType.PAGE_NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PageControllerTest extends ServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CacheUpdateService cacheUpdateService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExcelPostService excelPostService;

    @Autowired
    private ExcelNutritionService excelNutritionService;

    @Autowired
    private ExcelInspectionService excelInspectionService;

    public Member 회원1;

    public String 회원1엑세스토큰;

    @BeforeEach
    public void setUp() throws IOException {
        List<PregnancyCreateRequestDto> 임신중아이리스트 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 3);
        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트, null);
        회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());

        flushAndClear();

        String route = "validate/post/success/post_valid.xlsx";
        excelPostService.createPost(route);
        flushAndClear();

        String nutritionRoute = "validate/nutrition/success/nutrition_valid.xlsx";
        excelNutritionService.createNutrition(nutritionRoute, TYPE_PREGNANCY_NUTRITION);
        flushAndClear();

        String inspectionRoute = "validate/inspection/success/inspection_valid.xlsx";
        excelInspectionService.createInspection(inspectionRoute);
        flushAndClear();

        TokenResponseDto 회원1토큰 = loginService.createToken(회원1);
        회원1엑세스토큰 = 회원1토큰.getAccessToken();

        flushAndClear();

        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    @DisplayName("1.GET /page - 해피 케이스 - 포스트 페이지 조회")
    void 포스트_페이지_조회() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청 = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromPath("/page")
                .queryParam("type", TYPE_PREGNANCY_GUIDE)
                .queryParam("period", FETAL_PERIOD_5_8)
                .toUriString();

        ResponseEntity<PageResponseDto> response = restTemplate.exchange(
                url, HttpMethod.GET, 요청, PageResponseDto.class
        );
        PageResponseDto 응답 = response.getBody();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(응답.getVersion()).isEqualTo(DEFAULT_PAGE_VERSION);
        assertThat(응답.getPagePeriod()).isEqualTo(FETAL_PERIOD_5_8);
        assertThat(응답.getPageType()).isEqualTo(TYPE_PREGNANCY_GUIDE);
        assertThat(응답.getCategoryList().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("2.GET /page - 해피 케이스 - 영양 페이지 조회")
    void 영양_페이지_조회() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청 = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromPath("/page")
                .queryParam("type", TYPE_PREGNANCY_NUTRITION)
                .queryParam("period", PERIOD_TOTAL)
                .toUriString();

        ResponseEntity<PageResponseDto> response = restTemplate.exchange(
                url, HttpMethod.GET, 요청, PageResponseDto.class
        );
        PageResponseDto 응답 = response.getBody();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(응답.getVersion()).isEqualTo(DEFAULT_PAGE_VERSION);
        assertThat(응답.getPagePeriod()).isEqualTo(PERIOD_TOTAL);
        assertThat(응답.getPageType()).isEqualTo(TYPE_PREGNANCY_NUTRITION);
        assertThat(응답.getCategoryList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("3.GET /page - 해피 케이스 - 검사 페이지 조회")
    void 검사_페이지_조회() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청 = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromPath("/page")
                .queryParam("type", TYPE_INSPECTION)
                .queryParam("period", PERIOD_TOTAL)
                .toUriString();

        ResponseEntity<PageResponseDto> response = restTemplate.exchange(
                url, HttpMethod.GET, 요청, PageResponseDto.class
        );
        PageResponseDto 응답 = response.getBody();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(응답.getVersion()).isEqualTo(DEFAULT_PAGE_VERSION);
        assertThat(응답.getPagePeriod()).isEqualTo(PERIOD_TOTAL);
        assertThat(응답.getPageType()).isEqualTo(TYPE_INSPECTION);
        assertThat(응답.getCategoryList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("4.GET /page - 해피 케이스 - 없는 페이지 조회")
    void 없는_페이지_조회() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청 = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromPath("/page")
                .queryParam("type", 0)
                .queryParam("period", 0)
                .toUriString();

        ResponseEntity<ErrorResult> response = restTemplate.exchange(
                url, HttpMethod.GET, 요청, ErrorResult.class
        );
        ErrorResult 응답 = response.getBody();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(응답.getCode()).isEqualTo(PAGE_NOT_EXIST.getCode());
    }

    @Test
    @DisplayName("5.GET /page - 해피 케이스 - 버전체크 : 동일한 버전 조회시 NULL 반환")
    void 동일한_버전_조회시_null값_반환() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청 = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromPath("/page")
                .queryParam("type", TYPE_PREGNANCY_GUIDE)
                .queryParam("period", FETAL_PERIOD_5_8)
                .toUriString();

        ResponseEntity<PageResponseDto> response = restTemplate.exchange(
                url, HttpMethod.GET, 요청, PageResponseDto.class
        );
        PageResponseDto 응답 = response.getBody();
        Integer 기존버전 = 응답.getVersion();

        //when
        String urlV2 = UriComponentsBuilder.fromPath("/page")
                .queryParam("type", TYPE_PREGNANCY_GUIDE)
                .queryParam("period", FETAL_PERIOD_5_8)
                .queryParam("version", 기존버전)
                .toUriString();

        ResponseEntity<PageResponseDto> responseV2 = restTemplate.exchange(
                urlV2, HttpMethod.GET, 요청, PageResponseDto.class
        );
        PageResponseDto 응답V2 = responseV2.getBody();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(응답V2).isNull();
    }

    @Test
    @DisplayName("6.GET /page - 해피 케이스 - 버전체크 : 버전 업데이트시 새 응답 반환")
    void 버전_업데이트시_새응답_반환() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청 = new HttpEntity<>(headers);

        String url = UriComponentsBuilder.fromPath("/page")
                .queryParam("type", TYPE_PREGNANCY_GUIDE)
                .queryParam("period", FETAL_PERIOD_5_8)
                .toUriString();

        //when
        cacheUpdateService.updateCachedItem();

        ResponseEntity<PageResponseDto> response = restTemplate.exchange(
                url, HttpMethod.GET, 요청, PageResponseDto.class
        );

        String urlV2 = UriComponentsBuilder.fromPath("/page")
                .queryParam("type", TYPE_PREGNANCY_GUIDE)
                .queryParam("period", FETAL_PERIOD_5_8)
                .queryParam("version", DEFAULT_PAGE_VERSION)
                .toUriString();

        ResponseEntity<PageResponseDto> responseV2 = restTemplate.exchange(
                urlV2, HttpMethod.GET, 요청, PageResponseDto.class
        );
        PageResponseDto 응답V2 = responseV2.getBody();

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(응답V2).isNotNull();
        assertThat(응답V2.getVersion()).isEqualTo(DEFAULT_PAGE_VERSION+1);
    }
}