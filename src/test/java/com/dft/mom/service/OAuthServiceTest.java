package com.dft.mom.service;

import com.dft.mom.ServiceTest;
import com.dft.mom.domain.service.CacheOAuthService;
import com.dft.mom.web.exception.member.MemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.web.exception.ExceptionType.*;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
@ActiveProfiles("test")
@SpringBootTest()
public class OAuthServiceTest extends ServiceTest {

    @Autowired
    private CacheOAuthService authService;

    @BeforeEach
    public void setUp() {}

    @Test
    @DisplayName("1. 어드민과 회원은 무한접근이 가능하다.")
    public void 회원은_무한접근이_가능하다() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                authService.increaseAndValidate("1", ADMIN_STR);
                authService.increaseAndValidate("2", MEMBER_STR);
            }
        });
    }

    @Test
    @DisplayName("1. 비회원은 3번까지 접근이 가능하다.")
    public void 비회원은_다섯번까지_접근이_가능하다() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 3; i++) {
                authService.increaseAndValidate("1", NON_MEMBER_STR);
            }
        });

        MemberException ex = assertThrows(MemberException.class, () -> authService.increaseAndValidate("1", NON_MEMBER_STR));
        assertEquals(MORE_FOR_MEMBER.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("2. 회원은 15번까지 접근이 가능하다.")
    public void 비회원은_15번까지_접근이_가능하다() {
        assertDoesNotThrow(() -> {
            for (int i = 0; i < 15; i++) {
                authService.increaseAndValidate("1", MEMBER_STR);
            }
        });

        MemberException ex = assertThrows(MemberException.class, () -> authService.increaseAndValidate("1", MEMBER_STR));
        assertEquals(QUOTA_EXCEED.getCode(), ex.getCode());
    }
}