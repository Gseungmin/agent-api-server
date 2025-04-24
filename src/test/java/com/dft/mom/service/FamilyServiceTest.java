package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.repository.FamilyRepository;
import com.dft.mom.domain.service.FamilyService;
import com.dft.mom.domain.service.MemberService;
import com.dft.mom.web.exception.member.FamilyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.dft.mom.CreateUtil.*;
import static com.dft.mom.web.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class FamilyServiceTest extends ServiceTest {

    @Autowired
    private FamilyRepository familyRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private FamilyService familyService;

    public Member 회원1;
    public Member 회원2;
    public Member 회원3;
    public Member 회원4;
    public Member 회원5;

    @BeforeEach
    public void setUp() {
        List<PregnancyCreateRequestDto> 임신중아이리스트1 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 3);
        List<PregnancyCreateRequestDto> 임신중아이리스트2 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(10), null, 1);
        ParentingCreateRequestDto 태어난아이1 = createParentingCreateRequestDto("아이이름1", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이2 = createParentingCreateRequestDto("아이이름2", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이3 = createParentingCreateRequestDto("아이이름3", LocalDate.now().minusDays(8));
        ParentingCreateRequestDto 태어난아이4 = createParentingCreateRequestDto("아이이름4", LocalDate.now().minusDays(22));

        MemberCreateRequestDto 회원생성요청1 = createMemberCreateRequestDto(null, 임신중아이리스트1, null);
        MemberCreateRequestDto 회원생성요청2 = createMemberCreateRequestDto(null, 임신중아이리스트2, null);
        MemberCreateRequestDto 회원생성요청3 = createMemberCreateRequestDto(null, null, List.of(태어난아이1));
        MemberCreateRequestDto 회원생성요청4 = createMemberCreateRequestDto(null, null, List.of(태어난아이2));
        MemberCreateRequestDto 회원생성요청5 = createMemberCreateRequestDto(null, null, List.of(태어난아이3, 태어난아이4));

        회원1 = memberService.createMember(회원생성요청1, UUID.randomUUID().toString());
        회원2 = memberService.createMember(회원생성요청2, UUID.randomUUID().toString());
        회원3 = memberService.createMember(회원생성요청3, UUID.randomUUID().toString());
        회원4 = memberService.createMember(회원생성요청4, UUID.randomUUID().toString());
        회원5 = memberService.createMember(회원생성요청5, UUID.randomUUID().toString());

        flushAndClear();
    }

    @Test
    @DisplayName("1. 가족 연결 - 해피 케이스 - 1. 기존 가족 연결을 해제하고 새 연결을 할 수 있다.")
    public void 가족_연결_추가() {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());
        Member 회원3재조회 = memberService.getMember(회원3.getId());
        Member 회원4재조회 = memberService.getMember(회원4.getId());
        Member 회원5재조회 = memberService.getMember(회원5.getId());

        Family 가족1 = 회원1재조회.getFamily();
        Family 가족2 = 회원2재조회.getFamily();
        Family 가족3 = 회원3재조회.getFamily();
        Family 가족4 = 회원4재조회.getFamily();
        Family 가족5 = 회원5재조회.getFamily();

        //when
        familyService.connectFamily(회원1재조회, 가족2);
        familyService.connectFamily(회원3재조회, 가족4);

        flushAndClear();

        List<Member> 가족1회원리스트 = memberService.getMemberListByFamilyId(가족1.getId());
        List<Member> 가족2회원리스트 = memberService.getMemberListByFamilyId(가족2.getId());
        List<Member> 가족3회원리스트 = memberService.getMemberListByFamilyId(가족3.getId());
        List<Member> 가족4회원리스트 = memberService.getMemberListByFamilyId(가족4.getId());
        List<Member> 가족5회원리스트 = memberService.getMemberListByFamilyId(가족5.getId());

        Optional<Family> 가족1재조회 = familyRepository.findById(가족1.getId());
        Optional<Family> 가족2재조회 = familyRepository.findById(가족2.getId());
        Optional<Family> 가족3재조회 = familyRepository.findById(가족3.getId());
        Optional<Family> 가족4재조회 = familyRepository.findById(가족4.getId());
        Optional<Family> 가족5재조회 = familyRepository.findById(가족5.getId());

        //then
        assertThat(가족1회원리스트.size()).isEqualTo(0);
        assertThat(가족2회원리스트.size()).isEqualTo(2);
        assertThat(가족3회원리스트.size()).isEqualTo(0);
        assertThat(가족4회원리스트.size()).isEqualTo(2);
        assertThat(가족5회원리스트.size()).isEqualTo(1);

        assertThat(가족1재조회.get().getMemberList().size()).isEqualTo(0);
        assertThat(가족2재조회.get().getMemberList().size()).isEqualTo(2);
        assertThat(가족3재조회.get().getMemberList().size()).isEqualTo(0);
        assertThat(가족4재조회.get().getMemberList().size()).isEqualTo(2);
        assertThat(가족5재조회.get().getMemberList().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("1. 가족 연결 - 엣지 케이스 - 2. 기존 가족에 그대로 연결할 수 없다.")
    public void 기존_가족에_연결할_수없다() {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());

        Family 가족1 = 회원1재조회.getFamily();
        Family 가족2 = 회원2재조회.getFamily();

        //when then
        FamilyException exception1 = assertThrows(FamilyException.class, () -> {
            familyService.connectFamily(회원1재조회, 가족1);
        });

        FamilyException exception2 = assertThrows(FamilyException.class, () -> {
            familyService.connectFamily(회원2재조회, 가족2);
        });

        assertEquals(ALREADY_CONNECTED_FAMILY.getCode(), exception1.getCode());
        assertEquals(ALREADY_CONNECTED_FAMILY.getErrorMessage(), exception1.getErrorMessage());
        assertEquals(ALREADY_CONNECTED_FAMILY.getCode(), exception2.getCode());
        assertEquals(ALREADY_CONNECTED_FAMILY.getErrorMessage(), exception2.getErrorMessage());
    }


    @Test
    @DisplayName("2. 가족 연결 해제 - 해피 케이스 - 1. 기존 가족 연결을 해제할 수 있다.")
    public void 가족_연결_해제() {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());
        Member 회원3재조회 = memberService.getMember(회원3.getId());
        Member 회원4재조회 = memberService.getMember(회원4.getId());
        Member 회원5재조회 = memberService.getMember(회원5.getId());

        Family 가족1 = 회원1재조회.getFamily();
        Family 가족2 = 회원2재조회.getFamily();
        Family 가족3 = 회원3재조회.getFamily();
        Family 가족4 = 회원4재조회.getFamily();
        Family 가족5 = 회원5재조회.getFamily();

        //when
        familyService.disConnectFamily(회원1재조회);
        familyService.disConnectFamily(회원3재조회);

        flushAndClear();

        Member 회원1연결해제후재조회 = memberService.getMember(회원1.getId());
        Member 회원3연결해제후재조회 = memberService.getMember(회원3.getId());
        Family 가족6 = 회원1연결해제후재조회.getFamily();
        Family 가족7 = 회원3연결해제후재조회.getFamily();

        List<Member> 가족1회원리스트 = memberService.getMemberListByFamilyId(가족1.getId());
        List<Member> 가족2회원리스트 = memberService.getMemberListByFamilyId(가족2.getId());
        List<Member> 가족3회원리스트 = memberService.getMemberListByFamilyId(가족3.getId());
        List<Member> 가족4회원리스트 = memberService.getMemberListByFamilyId(가족4.getId());
        List<Member> 가족5회원리스트 = memberService.getMemberListByFamilyId(가족5.getId());
        List<Member> 가족6회원리스트 = memberService.getMemberListByFamilyId(가족6.getId());
        List<Member> 가족7회원리스트 = memberService.getMemberListByFamilyId(가족7.getId());

        //then
        assertThat(가족1회원리스트.size()).isEqualTo(0);
        assertThat(가족2회원리스트.size()).isEqualTo(1);
        assertThat(가족3회원리스트.size()).isEqualTo(0);
        assertThat(가족4회원리스트.size()).isEqualTo(1);
        assertThat(가족5회원리스트.size()).isEqualTo(1);
        assertThat(가족6회원리스트.size()).isEqualTo(1);
        assertThat(가족7회원리스트.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("2. 가족 연결 해제 - 해피 케이스 - 2. 가족 연결을 해제해도 새로운 가족이 생성된다.")
    public void 가족_연결_해제_새로운_가족_생성() {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());

        //when
        familyService.disConnectFamily(회원1재조회);
        flushAndClear();

        //then
        Member 회원1연결해제후재조회 = memberService.getMember(회원1.getId());
        Family 가족2 = 회원1연결해제후재조회.getFamily();
        Baby 새로운아이 = 가족2.getBabyList().get(0);
        assertThat(새로운아이.getName()).isEqualTo("김둥이");
    }

    @Test
    @DisplayName("3. 가족 조회 - 해피 케이스 - 1. 코드로 가족을 조회할 수 있다.")
    public void 코드로_가족을_조회() {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());

        Family 가족1 = 회원1재조회.getFamily();
        Family 가족2 = 회원2재조회.getFamily();

        String 가족1코드 = 가족1.getCode();
        String 가족2코드 = 가족2.getCode();

        //when
        Family 가족1_V2 = familyService.getFamilyByCode(가족1코드);
        Family 가족2_V2 = familyService.getFamilyByCode(가족2코드);

        //then
        assertThat(가족1_V2.getId()).isEqualTo(가족1.getId());
        assertThat(가족2_V2.getId()).isEqualTo(가족2.getId());
        assertThat(가족1.getId()).isNotEqualTo(가족2.getId());
    }

    @Test
    @DisplayName("3. 가족 조회 - 엣지 케이스 - 2. 없는 코드로 조회할 수 없다.")
    public void 없는코드로_코드로_가족을_조회할수없다() {
        //given when then
        FamilyException exception1 = assertThrows(FamilyException.class, () -> {
            familyService.getFamilyByCode("없는코드");
        });

        assertEquals(FAMILY_CODE_INVALID.getCode(), exception1.getCode());
        assertEquals(FAMILY_CODE_INVALID.getErrorMessage(), exception1.getErrorMessage());
    }

    @Test
    @DisplayName("4. 동시성 테스트 - 해피 케이스 - 1. 동시에 가족 연결을 할 수 있다.")
    public void 동시에_가족_연결이_가능하다() throws InterruptedException {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());
        Member 회원3재조회 = memberService.getMember(회원3.getId());

        Family 가족1 = 회원1재조회.getFamily();

        //when
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            latch.await();
            familyService.connectFamily(회원2재조회, 가족1);
            return null;
        });
        executor.submit(() -> {
            latch.await();
            familyService.connectFamily(회원3재조회, 가족1);
            return null;
        });

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        flushAndClear();

        //then
        List<Member> 가족1회원리스트 = memberService.getMemberListByFamilyId(가족1.getId());
        assertThat(가족1회원리스트.size()).isEqualTo(3);
    }
}