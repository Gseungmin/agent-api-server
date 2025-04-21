package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.dto.baby.req.BabyCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.BabyUpdateRequestDto;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.baby.res.BabyResponseDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.entity.post.BabyPage;
import com.dft.mom.domain.repository.PageRepository;
import com.dft.mom.domain.service.PageService;
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

import static com.dft.mom.CreateUtil.*;
import static com.dft.mom.domain.util.PostConstants.TOTAL_PAGE_SIZE;
import static com.dft.mom.web.exception.ExceptionType.BABY_NOT_EXIST;
import static com.dft.mom.web.exception.ExceptionType.UN_AUTH_BABY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class PageServiceTest extends ServiceTest {

    @Autowired
    private PageRepository pageRepository;

    @BeforeEach
    public void setUp() {
    }

    @Test
    @DisplayName("1. 페이지 생성 - 해피 케이스 - 1. 페이지가 추가되어있다.")
    public void 페이지_생성() {
        //given when
        List<BabyPage> 페이지_전체조회 = pageRepository.findAll();

        //then
        assertThat(페이지_전체조회.size()).isEqualTo(TOTAL_PAGE_SIZE);
    }
}