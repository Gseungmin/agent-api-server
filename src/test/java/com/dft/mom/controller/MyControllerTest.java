package com.dft.mom.controller;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberUpdateRequestDto;
import com.dft.mom.domain.dto.member.res.MemberResponseDto;
import com.dft.mom.domain.dto.member.res.MemberStatusResponseDto;
import com.dft.mom.domain.dto.member.res.MemberUpdateResponseDto;
import com.dft.mom.domain.dto.member.res.TokenResponseDto;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.repository.MemberRepository;
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
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.dft.mom.CreateUtil.*;
import static com.dft.mom.domain.util.CommonConstants.ACCESS_TOKEN;
import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.web.exception.ExceptionType.MULTI_LOGIN;
import static com.dft.mom.web.exception.ExceptionType.UN_AUTH_NON_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MyControllerTest extends ServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private MemberService memberService;

    @Autowired
    private TestRestTemplate restTemplate;

    public Member 회원1;
    public Member 회원2;

    public String 회원1엑세스토큰;
    public String 회원1리프레시토큰;

    public String 회원2엑세스토큰;
    public String 회원2리프레시토큰;

    @BeforeEach
    public void setUp() {
        List<PregnancyCreateRequestDto> 임신중아이리스트 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 3);
        ParentingCreateRequestDto 태어난아이1 = createParentingCreateRequestDto("아이이름1", LocalDate.now().minusDays(10));

        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트, null);
        MemberCreateRequestDto 회원생성요청2 = createMemberCreateRequestDto(null, null, List.of(태어난아이1));

        회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());
        회원2 = memberService.createMember(회원생성요청2, UUID.randomUUID().toString());

        flushAndClear();

        TokenResponseDto 회원1토큰 = loginService.createToken(회원1);
        TokenResponseDto 회원2토큰 = loginService.createToken(회원2);
        회원1엑세스토큰 = 회원1토큰.getAccessToken();
        회원1리프레시토큰 = 회원1토큰.getRefreshToken();
        회원2엑세스토큰 = 회원2토큰.getAccessToken();
        회원2리프레시토큰 = 회원2토큰.getRefreshToken();

        flushAndClear();

        TestTransaction.flagForCommit();
        TestTransaction.end();
    }

    @Test
    @DisplayName("1.GET /my - 해피 케이스 - 회원 조회")
    void 회원_조회() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        HttpEntity<Void> 요청 = new HttpEntity<>(headers);

        ResponseEntity<MemberResponseDto> response = restTemplate
                .exchange("/my", HttpMethod.GET, 요청, MemberResponseDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MemberResponseDto 응답 = response.getBody();
        assertThat(응답.getFamilyId()).isNotNull();
        assertThat(응답.getRole()).isEqualTo(MEMBER_STR);
        assertThat(응답.getBabyList().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("2.PATCH /my - 해피 케이스 - 회원 업데이트")
    void 회원_업데이트() {
        // given when
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(회원1엑세스토큰);
        LocalDate 생일 = LocalDate.now().minusYears(15);
        MemberUpdateRequestDto 업데이트정보 = new MemberUpdateRequestDto(
                "테스트1", MALE, MALE, 생일, "DEVICE", AGREE_ALL, DEFAULT_ALARM_LIST);
        HttpEntity<MemberUpdateRequestDto> 요청 = new HttpEntity<>(업데이트정보, headers);

        ResponseEntity<MemberUpdateResponseDto> response = restTemplate
                .exchange("/my", HttpMethod.PATCH, 요청, MemberUpdateResponseDto.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        MemberUpdateResponseDto 응답 = response.getBody();
        assertThat(응답.getName()).isEqualTo("테스트1");
        assertThat(응답.getRelation()).isEqualTo(MALE);
        assertThat(응답.getGender()).isEqualTo(MALE);
        assertThat(응답.getBirth()).isEqualTo(생일.toString());
    }
}