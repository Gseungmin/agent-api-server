package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberUpdateRequestDto;
import com.dft.mom.domain.dto.member.res.MemberResponseDto;
import com.dft.mom.domain.dto.member.res.TokenResponseDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.domain.entity.member.Auth;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.repository.FamilyRepository;
import com.dft.mom.domain.service.LoginService;
import com.dft.mom.domain.service.MemberService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.dft.mom.CreateUtil.*;
import static com.dft.mom.domain.util.EntityConstants.*;
import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class MemberServiceTest extends ServiceTest {

    @Autowired
    private LoginService loginService;

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private Key key;

    @BeforeEach
    public void setUp() {}

    @Test
    @DisplayName("1. 비회원 엑세스 토큰 발급 - 해피 케이스 - 1. 비회원은 서로 다른 엑세스 토큰을 발급받을 수 있다.")
    public void 비회원_엑세스_토큰_발급() {
        //given when
        Set<String> tokenList = new HashSet<>();

        int count = 10;
        for (int i = 0; i < count; i++) {
            TokenResponseDto response = loginService.createToken(UUID.randomUUID().toString());
            tokenList.add(response.getAccessToken());
            tokenList.add(response.getRefreshToken());
        }

        //then
        assertThat(tokenList.size()).isEqualTo(20);
    }

    @Test
    @DisplayName("1. 비회원 엑세스 토큰 발급 - 해피 케이스 - 2. 비회원의 토큰은 NON 권한을 가진다.")
    public void 비회원_엑세스_토큰_권한_체크() {
        //given
        TokenResponseDto response = loginService.createToken(UUID.randomUUID().toString());

        //when
        Claims accessTokenClaim = loginService.validateToken(key, response.getAccessToken());
        Claims refreshTokenClaim = loginService.validateToken(key, response.getRefreshToken());
        List<String> accessTokenRoles = accessTokenClaim.get("role", List.class);
        List<String> refreshTokenRoles = refreshTokenClaim.get("role", List.class);

        //then
        assertThat(accessTokenRoles.size()).isEqualTo(1);
        assertThat(refreshTokenRoles.size()).isEqualTo(1);
        assertThat(accessTokenRoles).contains(NON_MEMBER_STR);
        assertThat(refreshTokenRoles).contains(NON_MEMBER_STR);
    }

    @Test
    @DisplayName("2. 회원 생성 - 해피 케이스 - 1. 회원을 생성할 수 있다.")
    public void 회원을_생성할수있다() {
        //given
        List<PregnancyCreateRequestDto> 임신중아이리스트 =
                createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 5);

        MemberCreateRequestDto 회원생성요청 = createMemberCreateRequestDto(null, 임신중아이리스트, null);
        Member 회원1 = memberService.createMember(회원생성요청, UUID.randomUUID().toString());

        //when
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Auth 회원1인증정보 = 회원1재조회.getAuth();
        Family 가족1 = 회원1재조회.getFamily();
        List<Baby> 가족1아이들 = 가족1.getBabyList();

        //then
        assertThat(회원1재조회.getId()).isNotNull();
        assertThat(회원1재조회.getAgreeList()).isEqualTo(7);
        assertThat(회원1재조회.getGender()).isNull();
        assertThat(회원1재조회.getRelation()).isEqualTo(FEMALE);
        assertThat(회원1재조회.getProfileImage()).isNull();
        assertThat(회원1재조회.getSocialType()).isEqualTo("KAKAO");

        assertThat(회원1인증정보.getLastChanged()).isNull();
        assertThat(회원1인증정보.getDeleteDate()).isNull();
        assertThat(회원1인증정보.getPhoneNumber()).isNotNull();

        assertThat(가족1아이들.size()).isEqualTo(5);
    }

    @Test
    @DisplayName("2. 회원 생성 - 해피 케이스 - 2. 생성된 회원은 고유의 정보를 가진다.")
    public void 회원은_중복된_정보를_가지지않는다() {
        //given
        List<PregnancyCreateRequestDto> 임신중아이리스트 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 5);
        ParentingCreateRequestDto 태어난아이 = createParentingCreateRequestDto("아이이름", LocalDate.now().minusDays(10));

        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트, null);
        MemberCreateRequestDto 회원생성요청2 = createMemberCreateRequestDto(null, null, List.of(태어난아이));

        //when
        Member 회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());
        Member 회원2 = memberService.createMember(회원생성요청2, UUID.randomUUID().toString());

        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());

        Auth 회원1인증정보 = 회원1재조회.getAuth();
        Family 가족1 = 회원1재조회.getFamily();
        List<Baby> 가족1아이들 = 가족1.getBabyList();

        Auth 회원2인증정보 = 회원2재조회.getAuth();
        Family 가족2 = 회원2재조회.getFamily();
        List<Baby> 가족2아이들 = 가족2.getBabyList();

        //then
        assertThat(회원1재조회.getId()).isNotEqualTo(회원2재조회.getId());
        assertThat(회원1인증정보.getId()).isNotEqualTo(회원2인증정보.getId());
        assertThat(가족1.getId()).isNotEqualTo(가족2.getId());

        assertThat(가족1아이들.size()).isEqualTo(5);
        assertThat(가족2아이들.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("2. 회원 생성 - 해피 케이스 - 3. 초대 코드로 회원을 생성할 수 있다.")
    public void 초대코드로_회원을_생성할수있다() {
        //given
        List<PregnancyCreateRequestDto> 임신중아이리스트 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 5);
        ParentingCreateRequestDto 태어난아이1 = createParentingCreateRequestDto("아이이름1", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이2 = createParentingCreateRequestDto("아이이름2", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이3 = createParentingCreateRequestDto("아이이름3", LocalDate.now().minusDays(10));

        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트, null);
        MemberCreateRequestDto 회원생성요청2 = createMemberCreateRequestDto(null, null, List.of(태어난아이1));
        MemberCreateRequestDto 회원생성요청3 = createMemberCreateRequestDto(null, null, List.of(태어난아이2, 태어난아이3));
        MemberCreateRequestDto 회원생성요청4 = createMemberCreateRequestDto(null, null, null);
        MemberCreateRequestDto 회원생성요청5 = createMemberCreateRequestDto(null, null, null);

        Member 회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());
        Member 회원2 = memberService.createMember(회원생성요청2, UUID.randomUUID().toString());
        Member 회원3 = memberService.createMember(회원생성요청3, UUID.randomUUID().toString());

        flushAndClear();

        //when
        Member 회원2재조회 = memberService.getMember(회원2.getId());
        Member 회원3재조회 = memberService.getMember(회원3.getId());

        Family 가족2 = 회원2재조회.getFamily();
        Family 가족3 = 회원3재조회.getFamily();

        Member 회원4 = memberService.createMemberWithCode(가족2, 회원생성요청4, UUID.randomUUID().toString());
        Member 회원5 = memberService.createMemberWithCode(가족3, 회원생성요청5, UUID.randomUUID().toString());

        flushAndClear();

        Member 가족연결후회원1재조회 = memberService.getMember(회원1.getId());
        Member 가족연결후회원2재조회 = memberService.getMember(회원2.getId());
        Member 가족연결후회원3재조회 = memberService.getMember(회원3.getId());
        Member 가족연결후회원4재조회 = memberService.getMember(회원4.getId());
        Member 가족연결후회원5재조회 = memberService.getMember(회원5.getId());

        Family 가족1_V2 = 가족연결후회원1재조회.getFamily();
        Family 가족2_V2 = 가족연결후회원2재조회.getFamily();
        Family 가족3_V2 = 가족연결후회원3재조회.getFamily();
        Family 가족4_V2 = 가족연결후회원4재조회.getFamily();
        Family 가족5_V2 = 가족연결후회원5재조회.getFamily();

        List<Member> 가족1회원리스트 = memberService.getMemberListByFamilyId(가족1_V2.getId());
        List<Member> 가족2회원리스트 = memberService.getMemberListByFamilyId(가족2_V2.getId());
        List<Member> 가족3회원리스트 = memberService.getMemberListByFamilyId(가족3_V2.getId());
        List<Member> 가족4회원리스트 = memberService.getMemberListByFamilyId(가족4_V2.getId());
        List<Member> 가족5회원리스트 = memberService.getMemberListByFamilyId(가족5_V2.getId());

        List<Family> 모두가족리스트 = familyRepository.findAll();

        //then
        assertThat(가족2_V2.getId()).isEqualTo(가족4_V2.getId());
        assertThat(가족3_V2.getId()).isEqualTo(가족5_V2.getId());

        assertThat(가족1회원리스트.size()).isEqualTo(1);
        assertThat(가족2회원리스트.size()).isEqualTo(2);
        assertThat(가족3회원리스트.size()).isEqualTo(2);
        assertThat(가족4회원리스트.size()).isEqualTo(2);
        assertThat(가족5회원리스트.size()).isEqualTo(2);

        assertThat(모두가족리스트.size()).isEqualTo(5-1-1);
    }

    @Test
    @DisplayName("3. 회원 수정 - 해피 케이스 - 1. 회원 정보를 수정할 수 있다.")
    public void 회원정보_수정() {
        //given
        List<PregnancyCreateRequestDto> 임신중아이리스트 =
                createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 5);
        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트, null);
        Member 회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());
        Member 회원1재조회 = memberService.getMember(회원1.getId());

        //when
        LocalDate 업데이트생일 = LocalDate.now().minusDays(5);
        MemberUpdateRequestDto 업데이트정보 = new MemberUpdateRequestDto(
                "테스트1", MALE, MALE, 업데이트생일, "DEVICE", AGREE_ALL, DEFAULT_ALARM_LIST);
        memberService.updateProfile(회원1재조회, 업데이트정보);
        Member 업데이트된_회원재조회 = memberService.getMemberOnly(회원1.getId());

        //then
        assertThat(업데이트된_회원재조회.getId()).isEqualTo(회원1재조회.getId());
        assertThat(업데이트된_회원재조회.getBirth()).isEqualTo(업데이트생일);
        assertThat(업데이트된_회원재조회.getGender()).isEqualTo(MALE);
        assertThat(업데이트된_회원재조회.getRelation()).isEqualTo(MALE);
        assertThat(업데이트된_회원재조회.getDevice()).isEqualTo("DEVICE");
        assertThat(업데이트된_회원재조회.getAgreeList()).isEqualTo(AGREE_ALL);
        assertThat(업데이트된_회원재조회.getAlarmList()).isEqualTo(DEFAULT_ALARM_LIST);
    }


    @Test
    @DisplayName("4. 회원 조회 - 해피 케이스 - 1. 회원정보를 조회할 수 있다.")
    public void 회원을_조회할수있다() {
        //given
        List<PregnancyCreateRequestDto> 임신중아이리스트 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 5);
        ParentingCreateRequestDto 태어난아이1 = createParentingCreateRequestDto("아이이름1", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이2 = createParentingCreateRequestDto("아이이름2", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이3 = createParentingCreateRequestDto("아이이름3", LocalDate.now().minusDays(10));

        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트, null);
        MemberCreateRequestDto 회원생성요청2 = createMemberCreateRequestDto(null, null, List.of(태어난아이1));
        MemberCreateRequestDto 회원생성요청3 = createMemberCreateRequestDto(null, null, List.of(태어난아이2, 태어난아이3));
        MemberCreateRequestDto 회원생성요청4 = createMemberCreateRequestDto(null, null, null);
        MemberCreateRequestDto 회원생성요청5 = createMemberCreateRequestDto(null, null, null);

        Member 회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());
        Member 회원2 = memberService.createMember(회원생성요청2, UUID.randomUUID().toString());
        Member 회원3 = memberService.createMember(회원생성요청3, UUID.randomUUID().toString());

        flushAndClear();

        Member 회원2재조회 = memberService.getMember(회원2.getId());
        Member 회원3재조회 = memberService.getMember(회원3.getId());

        Family 가족2 = 회원2재조회.getFamily();
        Family 가족3 = 회원3재조회.getFamily();

        Member 회원4 = memberService.createMemberWithCode(가족2, 회원생성요청4, UUID.randomUUID().toString());
        Member 회원5 = memberService.createMemberWithCode(가족3, 회원생성요청5, UUID.randomUUID().toString());

        flushAndClear();

        //when
        MemberResponseDto 가족연결후회원1재조회 = memberService.getMemberResponse(회원1.getId());
        MemberResponseDto 가족연결후회원2재조회 = memberService.getMemberResponse(회원2.getId());
        MemberResponseDto 가족연결후회원3재조회 = memberService.getMemberResponse(회원3.getId());
        MemberResponseDto 가족연결후회원4재조회 = memberService.getMemberResponse(회원4.getId());
        MemberResponseDto 가족연결후회원5재조회 = memberService.getMemberResponse(회원5.getId());

        //then
        assertThat(회원1.getFamily().getId().toString()).isEqualTo(가족연결후회원1재조회.getFamilyId());
        assertThat(회원1.getAlarmList()).isEqualTo(가족연결후회원1재조회.getAlarmList());
        assertThat(회원1.getGender()).isEqualTo(가족연결후회원1재조회.getGender());
        assertThat(회원1.getName()).isEqualTo(가족연결후회원1재조회.getName());
        assertThat(임신중아이리스트.size()).isEqualTo(가족연결후회원1재조회.getBabyList().size());

        assertThat(회원2.getFamily().getId().toString()).isEqualTo(가족연결후회원2재조회.getFamilyId());
        assertThat(회원2.getAlarmList()).isEqualTo(가족연결후회원2재조회.getAlarmList());
        assertThat(회원2.getGender()).isEqualTo(가족연결후회원2재조회.getGender());
        assertThat(회원2.getName()).isEqualTo(가족연결후회원2재조회.getName());
        assertThat(1).isEqualTo(가족연결후회원2재조회.getBabyList().size());

        assertThat(회원3.getFamily().getId().toString()).isEqualTo(가족연결후회원3재조회.getFamilyId());
        assertThat(회원3.getAlarmList()).isEqualTo(가족연결후회원3재조회.getAlarmList());
        assertThat(회원3.getGender()).isEqualTo(가족연결후회원3재조회.getGender());
        assertThat(회원3.getName()).isEqualTo(가족연결후회원3재조회.getName());
        assertThat(2).isEqualTo(가족연결후회원3재조회.getBabyList().size());

        assertThat(회원4.getFamily().getId().toString()).isEqualTo(가족연결후회원4재조회.getFamilyId());
        assertThat(회원4.getAlarmList()).isEqualTo(가족연결후회원4재조회.getAlarmList());
        assertThat(회원4.getGender()).isEqualTo(가족연결후회원4재조회.getGender());
        assertThat(회원4.getName()).isEqualTo(가족연결후회원4재조회.getName());
        assertThat(1).isEqualTo(가족연결후회원4재조회.getBabyList().size());

        assertThat(회원5.getFamily().getId().toString()).isEqualTo(가족연결후회원5재조회.getFamilyId());
        assertThat(회원5.getAlarmList()).isEqualTo(가족연결후회원5재조회.getAlarmList());
        assertThat(회원5.getGender()).isEqualTo(가족연결후회원5재조회.getGender());
        assertThat(회원5.getName()).isEqualTo(가족연결후회원5재조회.getName());
        assertThat(2).isEqualTo(가족연결후회원5재조회.getBabyList().size());
    }
}