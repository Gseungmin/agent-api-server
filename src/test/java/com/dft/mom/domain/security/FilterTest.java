package com.dft.mom.domain.security;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.res.MemberStatusResponseDto;
import com.dft.mom.domain.dto.member.res.TokenResponseDto;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.service.LoginService;
import com.dft.mom.domain.service.MemberService;
import com.dft.mom.web.exception.ErrorResult;
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
import static com.dft.mom.domain.util.EntityConstants.MEMBER_STR;
import static com.dft.mom.domain.util.EntityConstants.NON_MEMBER_STR;
import static com.dft.mom.web.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilterTest extends ServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TestRestTemplate restTemplate;

    public Member 회원1;
    public String 비회원엑세스토큰;
    public String 비회원리프리시토큰;

    public String 회원1엑세스토큰;
    public String 회원1리프레시토큰;

    @BeforeEach
    public void setUp() {
        List<PregnancyCreateRequestDto> 임신중아이리스트 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 3);
        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트, null);

        회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());

        flushAndClear();

        TokenResponseDto 비회원토큰 = loginService.createToken(UUID.randomUUID().toString(), NON_MEMBER_STR);
        TokenResponseDto 회원1토큰 = loginService.createToken(회원1);
        비회원엑세스토큰 = 비회원토큰.getAccessToken();
        비회원리프리시토큰 = 비회원토큰.getRefreshToken();
        회원1엑세스토큰 = 회원1토큰.getAccessToken();
        회원1리프레시토큰 = 회원1토큰.getRefreshToken();

        flushAndClear();
    }

    @Test
    @DisplayName("1.GET /oauth - 해피 케이스 - 비회원 보안키 검증 성공")
    void 보안키_검증에_성공하면_요청이_성공한다() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(비회원엑세스토큰);
        headers.add("X-Internal-Secret", "abcde");
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate
                .exchange("/oauth", HttpMethod.GET, 요청헤더, Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("2.GET /oauth - 엣지 케이스 - 비회원 보안키 검증 실패")
    void 보안키_검증에_실패하면_예외가_발생한다() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(비회원엑세스토큰);
        headers.add("X-Internal-Secret", "abc");
        HttpEntity<Void> 요청헤더 = new HttpEntity<>(headers);

        ResponseEntity<ErrorResult> response = restTemplate
                .exchange("/oauth", HttpMethod.GET, 요청헤더, ErrorResult.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getCode()).isEqualTo(NOT_ALLOWED_LLM_SERVER.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo(NOT_ALLOWED_LLM_SERVER.getErrorMessage());
    }
}