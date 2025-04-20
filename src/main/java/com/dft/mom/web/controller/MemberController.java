package com.dft.mom.web.controller;

import com.dft.mom.domain.dto.member.res.MemberStatusResponseDto;
import com.dft.mom.domain.dto.member.res.TokenResponseDto;
import com.dft.mom.domain.redis.LoginRedisService;
import com.dft.mom.domain.service.LoginService;
import com.dft.mom.domain.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

import static com.dft.mom.domain.function.FunctionUtil.getToken;
import static com.dft.mom.domain.util.CommonConstants.REFRESH_TOKEN;
import static com.dft.mom.domain.util.EntityConstants.NON_MEMBER_STR;
import static com.dft.mom.domain.validator.MemberValidator.*;
import static java.lang.Long.parseLong;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {

    private final LoginService loginService;
    private final RoleService roleService;

    /*카카오 로그인*/
    @PostMapping("/login/kakao")
    public void kakaoLogin() {}

    /*애플 로그인*/
    @PostMapping("/login/apple")
    public void appleLogin() {}

    /*비회원 토큰 발급*/
    @PostMapping("/login/non")
    public TokenResponseDto unAuthLogin() {
        String memberId = UUID.randomUUID().toString();
        return loginService.createToken(memberId);
    }

    /*토큰 검증*/
    @GetMapping("/validate")
    public MemberStatusResponseDto validateToken(Authentication authentication, HttpServletRequest request) {
        validateAuthentication(authentication, request);
        String role = roleService.getMemberRole(authentication);
        return new MemberStatusResponseDto(role);
    }

    /*토큰 재발급*/
    @GetMapping("/reissue")
    public TokenResponseDto reIssueToken(Authentication authentication, HttpServletRequest request) {
        validateAuthentication(authentication, request);
        String memberRole = roleService.getMemberRole(authentication);

        if (Objects.equals(memberRole, NON_MEMBER_STR)) {
            return loginService.createToken(authentication.getName());
        }

        Long memberId = parseLong(authentication.getName());
        String token = getToken(request);
        loginService.validateLogin(token, memberId.toString(), REFRESH_TOKEN);
        return loginService.createToken(authentication.getName());
    }
}
