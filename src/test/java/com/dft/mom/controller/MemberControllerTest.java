package com.dft.mom.controller;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.baby.req.BabyCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.BabyUpdateRequestDto;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.baby.res.BabyResponseDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.res.MemberStatusResponseDto;
import com.dft.mom.domain.dto.member.res.TokenResponseDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.service.BabyService;
import com.dft.mom.domain.service.LoginService;
import com.dft.mom.domain.service.MemberService;
import com.dft.mom.web.exception.CommonException;
import com.dft.mom.web.exception.ErrorResult;
import com.dft.mom.web.exception.member.FamilyException;
import com.dft.mom.web.exception.member.MemberException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.dft.mom.CreateUtil.*;
import static com.dft.mom.domain.util.CommonConstants.ACCESS_TOKEN;
import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.web.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberControllerTest extends ServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Key key;

    public Member 회원1;
    public Member 회원2;
    public Member 회원3;
    public String 비회원엑세스토큰;
    public String 비회원리프리시토큰;

    public String 회원1엑세스토큰;
    public String 회원1리프레시토큰;

    public String 회원2엑세스토큰;
    public String 회원2리프레시토큰;

    @BeforeEach
    public void setUp() {
        List<PregnancyCreateRequestDto> 임신중아이리스트 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 3);
        ParentingCreateRequestDto 태어난아이1 = createParentingCreateRequestDto("아이이름1", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이2 = createParentingCreateRequestDto("아이이름2", LocalDate.now().minusDays(10));

        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트, null);
        MemberCreateRequestDto 회원생성요청2 = createMemberCreateRequestDto(null, null, List.of(태어난아이1));
        MemberCreateRequestDto 회원생성요청3 = createMemberCreateRequestDto(null, null, List.of(태어난아이2));

        회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());
        회원2 = memberService.createMember(회원생성요청2, UUID.randomUUID().toString());
        회원3 = memberService.createMember(회원생성요청3, UUID.randomUUID().toString());

        flushAndClear();

        TokenResponseDto 비회원토큰 = loginService.createToken(UUID.randomUUID().toString(), NON_MEMBER_STR);
        TokenResponseDto 회원1토큰 = loginService.createToken(회원1);
        TokenResponseDto 회원2토큰 = loginService.createToken(회원2);
        비회원엑세스토큰 = 비회원토큰.getAccessToken();
        비회원리프리시토큰 = 비회원토큰.getRefreshToken();
        회원1엑세스토큰 = 회원1토큰.getAccessToken();
        회원1리프레시토큰 = 회원1토큰.getRefreshToken();
        회원2엑세스토큰 = 회원2토큰.getAccessToken();
        회원2리프레시토큰 = 회원2토큰.getRefreshToken();

        flushAndClear();
    }

    @Test
    @DisplayName("1.POST /auth/login/non - 해피 케이스 - 비회원 토큰 발급")
    void 비회원토큰발급() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);

        // when
        ResponseEntity<TokenResponseDto> response = restTemplate
                .postForEntity("/auth/login/non", 요청헤더, TokenResponseDto.class);

        TokenResponseDto body = response.getBody();
        Claims accessTokenClaim = loginService.validateToken(key, body.getAccessToken());
        Claims refreshTokenClaim = loginService.validateToken(key, body.getRefreshToken());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(body).isNotNull();
        List<String> accessTokenRoles = accessTokenClaim.get("role", List.class);
        List<String> refreshTokenRoles = refreshTokenClaim.get("role", List.class);
        assertThat(accessTokenRoles.size()).isEqualTo(1);
        assertThat(refreshTokenRoles.size()).isEqualTo(1);
        assertThat(accessTokenRoles).contains(NON_MEMBER_STR);
        assertThat(refreshTokenRoles).contains(NON_MEMBER_STR);
    }

    @Test
    @DisplayName("2.GET /auth/validate - 해피 케이스 - 비회원 토큰 검증")
    void 비회원토큰검증() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(비회원엑세스토큰);
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);

        ResponseEntity<MemberStatusResponseDto> response = restTemplate
                .exchange("/auth/validate", HttpMethod.GET, 요청헤더, MemberStatusResponseDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MemberStatusResponseDto 비회원상태 = response.getBody();
        assertThat(비회원상태).isNotNull();
        assertThat(비회원상태.getRole()).isEqualTo(NON_MEMBER_STR);
    }

    @Test
    @DisplayName("3.GET /auth/validate - 해피 케이스 - 회원 토큰 검증")
    void 회원토큰검증() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);

        ResponseEntity<MemberStatusResponseDto> response = restTemplate
                .exchange("/auth/validate", HttpMethod.GET, 요청헤더, MemberStatusResponseDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MemberStatusResponseDto 회원상태 = response.getBody();
        assertThat(회원상태).isNotNull();
        assertThat(회원상태.getRole()).isEqualTo(MEMBER_STR);
    }

    @Test
    @DisplayName("4.GET /auth/reissue - 해피 케이스 - 비회원 토큰 재발급")
    void 비회원_토큰재발급() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(비회원리프리시토큰);
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);

        ResponseEntity<TokenResponseDto> response = restTemplate
                .exchange("/auth/reissue", HttpMethod.GET, 요청헤더, TokenResponseDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TokenResponseDto 응답 = response.getBody();
        Claims 엑세스토큰클레임 = loginService.validateToken(key, 응답.getAccessToken());
        Claims 리프레시토큰클레임 = loginService.validateToken(key, 응답.getRefreshToken());
        List<String> 엑세스토큰역할 = 엑세스토큰클레임.get("role", List.class);
        List<String> 리프레시토큰역할 = 리프레시토큰클레임.get("role", List.class);
        assertThat(엑세스토큰역할.size()).isEqualTo(1);
        assertThat(리프레시토큰역할.size()).isEqualTo(1);
        assertThat(엑세스토큰역할).contains(NON_MEMBER_STR);
        assertThat(리프레시토큰역할).contains(NON_MEMBER_STR);
    }

    @Test
    @DisplayName("5.GET /auth/reissue - 해피 케이스 - 회원 토큰 재발급")
    void 회원_토큰재발급() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1리프레시토큰);
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);

        ResponseEntity<TokenResponseDto> response = restTemplate
                .exchange("/auth/reissue", HttpMethod.GET, 요청헤더, TokenResponseDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TokenResponseDto 응답 = response.getBody();
        Claims 엑세스토큰클레임 = loginService.validateToken(key, 응답.getAccessToken());
        Claims 리프레시토큰클레임 = loginService.validateToken(key, 응답.getRefreshToken());
        List<String> 엑세스토큰역할 = 엑세스토큰클레임.get("role", List.class);
        List<String> 리프레시토큰역할 = 리프레시토큰클레임.get("role", List.class);
        assertThat(엑세스토큰역할.size()).isEqualTo(1);
        assertThat(리프레시토큰역할.size()).isEqualTo(1);
        assertThat(엑세스토큰역할).contains(MEMBER_STR);
        assertThat(리프레시토큰역할).contains(MEMBER_STR);
    }

    @Test
    @DisplayName("6.POST /auth/logout - 해피 케이스 - 비회원 예외 발생, 비회원이 접근 불가능한 경로")
    void 비회원_예외발생_비회원이_접근불가능한_경로() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(비회원엑세스토큰);
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);

        ResponseEntity<ErrorResult> response = restTemplate
                .exchange("/auth/logout", HttpMethod.POST, 요청헤더, ErrorResult.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(UN_AUTH_NON_MEMBER.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo(UN_AUTH_NON_MEMBER.getErrorMessage());
    }

    @Test
    @DisplayName("7.POST /auth/logout - 해피 케이스 - 회원 로그아웃")
    void 회원_로그아웃() {
        // given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);
        restTemplate.exchange("/auth/logout", HttpMethod.POST, 요청헤더, TokenResponseDto.class);

        //when then
        MemberException exception = assertThrows(MemberException.class, () -> {
            loginService.validateLogin(회원1엑세스토큰, 회원1.getId().toString(), ACCESS_TOKEN);
        });

        assertEquals(MULTI_LOGIN.getCode(), exception.getCode());
    }
}