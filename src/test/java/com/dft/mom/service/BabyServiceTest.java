package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.baby.req.BabyCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.BabyUpdateRequestDto;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.baby.res.BabyResponseDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.service.BabyService;
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
import java.util.UUID;

import static com.dft.mom.CreateUtil.*;
import static com.dft.mom.web.exception.ExceptionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class BabyServiceTest extends ServiceTest {

    @Autowired
    private BabyService babyService;

    @Autowired
    private MemberService memberService;

    public Member 회원1;
    public Member 회원2;
    public Member 회원3;

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
    }

    @Test
    @DisplayName("1. 아이 추가 - 해피 케이스 - 1. 아이를 추가할 수 있다.")
    public void 아이를_생성할수있다() {
        //given
        List<PregnancyCreateRequestDto> 임신중아이리스트2 = createPregnancyListCreateRequestDto(LocalDate.now().plusDays(30), null, 2);
        ParentingCreateRequestDto 태어난아이3 = createParentingCreateRequestDto("아이이름3", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이4 = createParentingCreateRequestDto("아이이름4", LocalDate.now().minusDays(10));
        ParentingCreateRequestDto 태어난아이5 = createParentingCreateRequestDto("아이이름5", LocalDate.now().minusDays(10));
        BabyCreateRequestDto 아이생성요청1 = new BabyCreateRequestDto(null, List.of(태어난아이3));
        BabyCreateRequestDto 아이생성요청2 = new BabyCreateRequestDto(null, List.of(태어난아이4, 태어난아이5));
        BabyCreateRequestDto 아이생성요청3 = new BabyCreateRequestDto(임신중아이리스트2, null);

        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());
        Member 회원3재조회 = memberService.getMember(회원3.getId());

        //when
        List<BabyResponseDto> 생성된아이리스트1 = babyService.createBaby(회원1재조회, 아이생성요청1);
        List<BabyResponseDto> 생성된아이리스트2 = babyService.createBaby(회원2재조회, 아이생성요청2);
        List<BabyResponseDto> 생성된아이리스트3 = babyService.createBaby(회원3재조회, 아이생성요청3);

        flushAndClear();

        List<BabyResponseDto> 가족1아이들 = babyService.getBabyList(회원1재조회.getFamily().getId());
        List<BabyResponseDto> 가족2아이들 = babyService.getBabyList(회원2재조회.getFamily().getId());
        List<BabyResponseDto> 가족3아이들 = babyService.getBabyList(회원3재조회.getFamily().getId());
        List<BabyResponseDto> 없는ID = babyService.getBabyList(1L);

        //then
        assertThat(생성된아이리스트1.size()).isEqualTo(1);
        assertThat(생성된아이리스트2.size()).isEqualTo(2);
        assertThat(생성된아이리스트3.size()).isEqualTo(2);

        assertThat(가족1아이들.size()).isEqualTo(1 + 3);
        assertThat(가족2아이들.size()).isEqualTo(2 + 1);
        assertThat(가족3아이들.size()).isEqualTo(2 + 1);
        assertThat(없는ID.size()).isEqualTo(0);
    }

    @Test
    @DisplayName("2. 아이 수정 - 해피 케이스 - 1. 아이 정보를 수정할 수 있다.")
    public void 아이_정보를_수정할수있다() {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());

        List<BabyResponseDto> 가족1아이들 = babyService.getBabyList(회원1재조회.getFamily().getId());
        List<BabyResponseDto> 가족2아이들 = babyService.getBabyList(회원2재조회.getFamily().getId());

        BabyResponseDto 가족1아이1 = 가족1아이들.get(0);
        BabyResponseDto 가족1아이2 = 가족1아이들.get(1);
        BabyResponseDto 가족2아이1 = 가족2아이들.get(0);

        flushAndClear();

        //when
        Baby 아이1 = babyService.getBaby(Long.valueOf(가족1아이1.getId()));
        Baby 아이2 = babyService.getBaby(Long.valueOf(가족1아이2.getId()));
        Baby 아이3 = babyService.getBaby(Long.valueOf(가족2아이1.getId()));

        BabyUpdateRequestDto 아이1업데이트 = createBabyUpdateRequestDto(아이1.getId(), 아이1.getFamily().getId(), "새이름1");
        BabyUpdateRequestDto 아이2업데이트 = createBabyUpdateRequestDto(아이2.getId(), 아이2.getFamily().getId(), "새이름2");
        BabyUpdateRequestDto 아이3업데이트 = createBabyUpdateRequestDto(아이3.getId(), 아이3.getFamily().getId(), "새이름3");

        BabyResponseDto 아이1업데이트후 = babyService.updateBaby(아이1, 아이1업데이트);
        BabyResponseDto 아이2업데이트후 = babyService.updateBaby(아이2, 아이2업데이트);
        BabyResponseDto 아이3업데이트후 = babyService.updateBaby(아이3, 아이3업데이트);

        //then
        assertThat(아이1업데이트후.getName()).isEqualTo("새이름1");
        assertThat(아이2업데이트후.getName()).isEqualTo("새이름2");
        assertThat(아이3업데이트후.getName()).isEqualTo("새이름3");
    }

    @Test
    @DisplayName("2. 아이 수정 - 엣지 케이스 - 2. 자기 아이 정보만 수정할 수 있다.")
    public void 자기_아이_정보만_수정할수있다() {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());

        List<BabyResponseDto> 가족1아이들 = babyService.getBabyList(회원1재조회.getFamily().getId());
        List<BabyResponseDto> 가족2아이들 = babyService.getBabyList(회원2재조회.getFamily().getId());

        BabyResponseDto 가족1아이1 = 가족1아이들.get(0);
        BabyResponseDto 가족2아이1 = 가족2아이들.get(0);

        flushAndClear();

        Baby 아이1 = babyService.getBaby(Long.valueOf(가족1아이1.getId()));
        Baby 아이2 = babyService.getBaby(Long.valueOf(가족2아이1.getId()));

        //when then
        FamilyException exception1 = assertThrows(FamilyException.class, () -> {
            BabyUpdateRequestDto 아이1업데이트 = createBabyUpdateRequestDto(아이1.getId(), 아이2.getFamily().getId(), "새이름1");
            babyService.updateBaby(아이1, 아이1업데이트);
        });

        FamilyException exception2 = assertThrows(FamilyException.class, () -> {
            BabyUpdateRequestDto 아이2업데이트 = createBabyUpdateRequestDto(아이2.getId(), 아이1.getFamily().getId(), "새이름1");
            babyService.updateBaby(아이2, 아이2업데이트);
        });

        assertEquals(UN_AUTH_BABY.getCode(), exception1.getCode());
        assertEquals(UN_AUTH_BABY.getErrorMessage(), exception1.getErrorMessage());
        assertEquals(UN_AUTH_BABY.getCode(), exception2.getCode());
        assertEquals(UN_AUTH_BABY.getErrorMessage(), exception2.getErrorMessage());
    }

    @Test
    @DisplayName("3. 아이 정보 삭제 - 해피 케이스 - 1. 아이 정보를 삭제할 수 있다.")
    public void 아이_정보를_삭제할수있다() {
        //given
        Member 회원1재조회 = memberService.getMember(회원1.getId());
        Member 회원2재조회 = memberService.getMember(회원2.getId());

        List<BabyResponseDto> 가족1아이들 = babyService.getBabyList(회원1재조회.getFamily().getId());
        List<BabyResponseDto> 가족2아이들 = babyService.getBabyList(회원2재조회.getFamily().getId());

        BabyResponseDto 가족1아이1 = 가족1아이들.get(0);
        BabyResponseDto 가족1아이2 = 가족1아이들.get(1);
        BabyResponseDto 가족2아이1 = 가족2아이들.get(0);

        flushAndClear();

        Baby 아이1 = babyService.getBaby(Long.valueOf(가족1아이1.getId()));
        Baby 아이2 = babyService.getBaby(Long.valueOf(가족1아이2.getId()));
        Baby 아이3 = babyService.getBaby(Long.valueOf(가족2아이1.getId()));

        //when
        babyService.deleteBaby(아이1, 아이1.getFamily().getId());

        flushAndClear();

        Baby 아이2_V2 = babyService.getBaby(Long.valueOf(가족1아이2.getId()));
        Baby 아이3_V2 = babyService.getBaby(Long.valueOf(가족2아이1.getId()));

        // then
        FamilyException exception1 = assertThrows(FamilyException.class, () -> {
            babyService.getBaby(Long.valueOf(가족1아이1.getId()));
        });

        assertEquals(BABY_NOT_EXIST.getCode(), exception1.getCode());
        assertEquals(BABY_NOT_EXIST.getErrorMessage(), exception1.getErrorMessage());
        assertThat(아이2_V2.getId()).isEqualTo(아이2.getId());
        assertThat(아이3_V2.getId()).isEqualTo(아이3.getId());
    }
}