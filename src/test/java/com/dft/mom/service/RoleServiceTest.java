package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.service.RoleService;
import com.dft.mom.web.exception.member.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.web.exception.ExceptionType.ADMIN_ONLY;
import static com.dft.mom.web.exception.ExceptionType.UN_AUTH_NON_MEMBER;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class RoleServiceTest extends ServiceTest {

    @Autowired
    private RoleService roleService;

    @BeforeEach
    public void setUp() {}

    @Test
    @DisplayName("1. 비회원 접근 제한 - 해피 케이스 - 1. 회원 및 어드민은 접근이 가능하다.")
    public void 회원은_접근이_가능하다() {
        assertDoesNotThrow(() -> roleService.validateNon(MEMBER_STR));
        assertDoesNotThrow(() -> roleService.validateNon(ADMIN_STR));
    }

    @Test
    @DisplayName("1. 비회원 접근 제한 - 엣지 케이스 - 2. 비회원 접근 시 에러가 발생한다.")
    public void 비회원은_접근이_불가능하다() {
        MemberException exception = assertThrows(MemberException.class, () -> {
            roleService.validateNon(NON_MEMBER_STR);
        });

        assertEquals(UN_AUTH_NON_MEMBER.getCode(), exception.getCode());
        assertEquals(UN_AUTH_NON_MEMBER.getErrorMessage(), exception.getErrorMessage());
    }

    @Test
    @DisplayName("2. 어드민 접근 제한 - 해피 케이스 - 1. 어드민은 접근이 가능하다.")
    public void 어드민은_접근이_가능하다() {
        assertDoesNotThrow(() -> roleService.validateAdmin(ADMIN_STR));
    }

    @Test
    @DisplayName("2. 어드민 접근 제한 - 엣지 케이스 - 1. 어드민을 제외하고 접근이 불가능하다.")
    public void 어드민_제외하고_접근이_불가능하다() {
        MemberException exception1 = assertThrows(MemberException.class, () -> {
            roleService.validateAdmin(NON_MEMBER_STR);
        });

        MemberException exception2 = assertThrows(MemberException.class, () -> {
            roleService.validateAdmin(MEMBER_STR);
        });

        assertEquals(ADMIN_ONLY.getCode(), exception1.getCode());
        assertEquals(ADMIN_ONLY.getErrorMessage(), exception1.getErrorMessage());
        assertEquals(ADMIN_ONLY.getCode(), exception2.getCode());
        assertEquals(ADMIN_ONLY.getErrorMessage(), exception2.getErrorMessage());
    }
}