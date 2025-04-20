package com.dft.mom.domain.validator;

import com.dft.mom.domain.dto.baby.req.BabyUpdateRequestDto;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberUpdateRequestDto;
import com.dft.mom.web.exception.CommonException;
import com.dft.mom.web.exception.member.FamilyException;
import com.dft.mom.web.exception.member.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.domain.validator.MemberValidator.*;
import static com.dft.mom.web.exception.ExceptionType.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class MemberValidatorTest {

    public MemberCreateRequestDto 일반_카카오_회원가입_회원;

    public PregnancyCreateRequestDto 임신아이정보1;
    public PregnancyCreateRequestDto 임신아이정보2;
    public ParentingCreateRequestDto 육아아이정보1;
    public ParentingCreateRequestDto 육아아이정보2;

    public List<PregnancyCreateRequestDto> 임신아이정보리스트;
    public List<ParentingCreateRequestDto> 육아아이정보리스트;

    public MemberUpdateRequestDto 회원_정보_수정;
    public BabyUpdateRequestDto 아기_정보_수정;

    @BeforeEach
    public void setUp() {
        임신아이정보1 = new PregnancyCreateRequestDto(
                "김두리", LocalDate.now().plusDays(100), LocalDate.now().minusDays(100));

        임신아이정보2 = new PregnancyCreateRequestDto(
                "김나리", LocalDate.now().plusDays(100), LocalDate.now().minusDays(100));

        육아아이정보1 = new ParentingCreateRequestDto("김두리", LocalDate.now().minusDays(30), FEMALE);
        육아아이정보2 = new ParentingCreateRequestDto("김나리", LocalDate.now().minusDays(30), FEMALE);

        임신아이정보리스트 = List.of(임신아이정보1, 임신아이정보2);
        육아아이정보리스트 = List.of(육아아이정보1, 육아아이정보2);

        일반_카카오_회원가입_회원 = new MemberCreateRequestDto(
                "엑세스 토큰", null, "김마미", FEMALE, AGREE_ALL, 임신아이정보리스트, new ArrayList<>());

        회원_정보_수정 = new MemberUpdateRequestDto(
                "김마미", FEMALE, FEMALE, LocalDate.now().minusYears(20), "iOS", AGREE_ALL, DEFAULT_ALARM_LIST);

        아기_정보_수정 = new BabyUpdateRequestDto(
                1L, 1L, "김두리", LocalDate.now().minusDays(20), 1230, FEMALE, BABY, null);
    }

    @Test
    @DisplayName("1. 회원 생성 - 해피케이스")
    void 회원_생성_DTO_검증_해피() {
        assertDoesNotThrow(() -> validateCreateMember(일반_카카오_회원가입_회원));
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 엑세스 토큰이 빈 문자열이면 예외")
    void 엑세스토큰_빈값() {
        // given
        일반_카카오_회원가입_회원.setAccessToken("");
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(SOCIAL_TOKEN_NEED.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 엑세스 토큰이 null 이면 예외")
    void 엑세스토큰_null() {
        // given
        일반_카카오_회원가입_회원.setAccessToken(null);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(SOCIAL_TOKEN_NEED.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 관계가 null 이면 예외")
    void 아이와의관계_null() {
        // given
        일반_카카오_회원가입_회원.setRelation(null);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_RELATION_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 관계가 허용되지 않은 값이면 예외")
    void 아이와의관계_잘못된값() {
        // given
        일반_카카오_회원가입_회원.setRelation(3);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_RELATION_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 약관 동의가 null 이면 예외")
    void 약관동의_null() {
        // given
        일반_카카오_회원가입_회원.setAgree(null);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_AGREE_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 약관 동의가 허용되지 않은 값이면 예외")
    void 약관동의_잘못된값() {
        // given
        일반_카카오_회원가입_회원.setAgree(8);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_AGREE_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 이름이 null 이면 예외")
    void 닉네임_null() {
        // given
        일반_카카오_회원가입_회원.setName(null);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_NAME_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 이름이 공백이면 예외")
    void 닉네임_공백() {
        // given
        일반_카카오_회원가입_회원.setName("  ");
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_NAME_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 이름 길이가 MAX_NAME 초과 시 예외")
    void 닉네임_길이초과() {
        // given
        일반_카카오_회원가입_회원.setName("가".repeat(MAX_NAME + 1));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_NAME_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 임신·육아 리스트가 동시에 존재하면 예외")
    void 임신육아리스트_둘다있음() {
        // given
        일반_카카오_회원가입_회원.setParentingList(육아아이정보리스트);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_BABY_LIST_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 임신·육아 리스트가 모두 비어 있으면 예외")
    void 임신육아리스트_둘다없음() {
        // given
        일반_카카오_회원가입_회원.setPregnancyList(new ArrayList<>());
        일반_카카오_회원가입_회원.setParentingList(new ArrayList<>());
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_BABY_LIST_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 임신 리스트 개수가 MAX_BABY_CREATE 초과 시 예외")
    void 임신아이_개수초과() {
        // given
        List<PregnancyCreateRequestDto> 임신리스트 = new ArrayList<>();
        for (int i = 0; i < MAX_BABY_CREATE + 1; i++) {
            임신리스트.add(new PregnancyCreateRequestDto("아가" + i, LocalDate.now().plusDays(50), LocalDate.now().minusDays(50)));
        }
        일반_카카오_회원가입_회원.setPregnancyList(임신리스트);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_MAX_BABY_LIST_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 육아 리스트 개수가 MAX_BABY_CREATE 초과 시 예외")
    void 육아아이_개수초과() {
        // given
        List<ParentingCreateRequestDto> 육아리스트 = new ArrayList<>();
        for (int i = 0; i < MAX_BABY_CREATE + 1; i++) {
            육아리스트.add(new ParentingCreateRequestDto("아가" + i, LocalDate.now().minusDays(20), FEMALE));
        }
        일반_카카오_회원가입_회원.setPregnancyList(new ArrayList<>());
        일반_카카오_회원가입_회원.setParentingList(육아리스트);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_MAX_BABY_LIST_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 임신한 아이의 예상 마지막 월경 모두 null 이면 예외")
    void 예상출산일_마지막월경일_날짜없음() {
        // given
        PregnancyCreateRequestDto 잘못된내용 = new PregnancyCreateRequestDto("아가", null, null);
        일반_카카오_회원가입_회원.setPregnancyList(List.of(잘못된내용));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 임신한 아이의 예상 분만일이 과거면 예외")
    void 예상출산일이_과거인경우() {
        // given
        PregnancyCreateRequestDto 잘못된내용 = new PregnancyCreateRequestDto("아가", LocalDate.now().minusDays(1), null);
        일반_카카오_회원가입_회원.setPregnancyList(List.of(잘못된내용));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 임신한 아이의 마지막 월경일이 미래면 예외")
    void 마지막월경일이_미래() {
        // given
        PregnancyCreateRequestDto 잘못된내용 = new PregnancyCreateRequestDto("아가", null, LocalDate.now().plusDays(1));
        일반_카카오_회원가입_회원.setPregnancyList(List.of(잘못된내용));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 육아 아이의 출생일이 미래면 예외")
    void 아이의_출생일이_미래() {
        // given
        ParentingCreateRequestDto 잘못된내용 = new ParentingCreateRequestDto("아가", LocalDate.now().plusDays(1), FEMALE);
        일반_카카오_회원가입_회원.setPregnancyList(new ArrayList<>());
        일반_카카오_회원가입_회원.setParentingList(List.of(잘못된내용));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("1. 회원 생성 - 엣지케이스 - 육아 아이의 성별 값이 잘못되면 예외")
    void 아이의_성별_잘못된경우() {
        // given
        ParentingCreateRequestDto 잘못된내용 = new ParentingCreateRequestDto("아가", LocalDate.now().minusDays(20), 2);
        일반_카카오_회원가입_회원.setPregnancyList(new ArrayList<>());
        일반_카카오_회원가입_회원.setParentingList(List.of(잘못된내용));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateCreateMember(일반_카카오_회원가입_회원));
        assertEquals(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), ex.getCode());
    }


    @Test
    @DisplayName("2. 회원 수정 - 해피케이스")
    void 회원정보_수정() {
        assertDoesNotThrow(() -> validateUpdateMember(회원_정보_수정));
    }

    @Test
    @DisplayName("2. 회원 수정 - 엣지케이스 - 관계설정이_잘못된경우")
    void 관계설정이_잘못된경우() {
        // given
        회원_정보_수정.setRelation(-1);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateUpdateMember(회원_정보_수정));
        assertEquals(MEMBER_CREATE_RELATION_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("2. 회원 수정 - 엣지케이스 - 약관동의 설정이 잘못된경우")
    void 약관동의수정이_잘못된경우() {
        // given
        회원_정보_수정.setAgreeList(16);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateUpdateMember(회원_정보_수정));
        assertEquals(MEMBER_CREATE_AGREE_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("2. 회원 수정 - 엣지케이스 - 생일이 오늘 이전일 경우")
    void 생일이_오늘이전일_경우() {
        // given
        회원_정보_수정.setBirth(LocalDate.now().plusDays(1));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateUpdateMember(회원_정보_수정));
        assertEquals(MEMBER_UPDATE_BIRTH_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("2. 회원 수정 - 엣지케이스 - 만 14세 미만일경우")
    void 만14세_미만일경우() {
        // given
        회원_정보_수정.setBirth(LocalDate.now().minusYears(13));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateUpdateMember(회원_정보_수정));
        assertEquals(MEMBER_UPDATE_BIRTH_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("2. 회원 수정 - 엣지케이스 - 이름이 비어있을 경우")
    void update_name_blank() {
        // given
        회원_정보_수정.setName("");
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateUpdateMember(회원_정보_수정));
        assertEquals(MEMBER_UPDATE_NAME_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("2. 회원 수정 - 엣지케이스 - 이름이 MAX NAME보다 클 경우")
    void 이름길이초과() {
        // given
        회원_정보_수정.setName("가".repeat(MAX_NAME + 1));
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateUpdateMember(회원_정보_수정));
        assertEquals(MEMBER_UPDATE_NAME_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("2. 회원 수정 - 엣지케이스 - 성별이 남녀가 아닌 경우")
    void 성별초과() {
        // given
        회원_정보_수정.setGender(2);
        // when then
        MemberException ex = assertThrows(MemberException.class, () -> validateUpdateMember(회원_정보_수정));
        assertEquals(MEMBER_UPDATE_GENDER_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("3. 아이 수정 - 해피케이스")
    void 아이수정() {
        assertDoesNotThrow(() -> validateUpdateBaby(아기_정보_수정));
    }

    @Test
    @DisplayName("3. 아이 수정 - 엣지케이스 - 마지막 월경일이 미래")
    void 마지막_월경일이_미래() {
        // given
        아기_정보_수정.setLastMenstrual(LocalDate.now().plusDays(1));
        // when then
        FamilyException ex = assertThrows(FamilyException.class, () -> validateUpdateBaby(아기_정보_수정));
        assertEquals(BABY_UPDATE_BIRTH_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("3. 아이 수정 - 엣지케이스 - 시간 범위 미만")
    void 시간범위미만() {
        // given
        아기_정보_수정.setBirthTime(MIN_TIME - 1);
        // when then
        FamilyException ex = assertThrows(FamilyException.class, () -> validateUpdateBaby(아기_정보_수정));
        assertEquals(BABY_UPDATE_BIRTH_TIME_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("3. 아이 수정 - 엣지케이스 - 시간 범위 초과")
    void 시간범위초과() {
        // given
        아기_정보_수정.setBirthTime(MAX_TIME);
        // when then
        FamilyException ex = assertThrows(FamilyException.class, () -> validateUpdateBaby(아기_정보_수정));
        assertEquals(BABY_UPDATE_BIRTH_TIME_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("3. 아이 수정 - 엣지케이스 - 임신중일때 생일이 과거인 경우")
    void 임신중인데_생일이_과거() {
        // given
        아기_정보_수정.setType(PREGNANT);
        아기_정보_수정.setBirth(LocalDate.now().minusDays(1));
        // when then
        FamilyException ex = assertThrows(FamilyException.class, () -> validateUpdateBaby(아기_정보_수정));
        assertEquals(BABY_UPDATE_BIRTH_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("3. 아이 수정 - 엣지케이스 - 태어난 아이의 생일이 미래")
    void 태어난_아이의_생일이_미래() {
        // given
        아기_정보_수정.setType(BABY);
        아기_정보_수정.setBirth(LocalDate.now().plusDays(1));
        // when then
        FamilyException ex = assertThrows(FamilyException.class, () -> validateUpdateBaby(아기_정보_수정));
        assertEquals(BABY_UPDATE_BIRTH_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("3. 아이 수정 - 엣지케이스 - 아이아이디정보미조회")
    void 아이_아이디_정보_미조회() {
        // given
        아기_정보_수정.setId(null);
        // when then
        CommonException ex = assertThrows(CommonException.class, () -> validateUpdateBaby(아기_정보_수정));
        assertEquals(ID_INVALID.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("3. 아이 수정 - 엣지케이스 - 가족아이디정보미조회")
    void 가족_아이디_정보_미조회() {
        // given
        아기_정보_수정.setFamilyId(null);
        // when then
        CommonException ex = assertThrows(CommonException.class, () -> validateUpdateBaby(아기_정보_수정));
        assertEquals(ID_INVALID.getCode(), ex.getCode());
    }
}