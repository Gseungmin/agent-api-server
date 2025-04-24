package com.dft.mom.web.controller;

import com.dft.mom.domain.dto.member.req.MemberAppleCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.res.MemberStatusResponseDto;
import com.dft.mom.domain.dto.member.res.TokenResponseDto;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.redis.LoginRedisService;
import com.dft.mom.domain.service.FamilyService;
import com.dft.mom.domain.service.LoginService;
import com.dft.mom.domain.service.MemberService;
import com.dft.mom.domain.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

import static com.dft.mom.domain.function.FunctionUtil.getToken;
import static com.dft.mom.domain.function.FunctionUtil.parseLong;
import static com.dft.mom.domain.util.CommonConstants.REFRESH_TOKEN;
import static com.dft.mom.domain.util.EntityConstants.MEMBER_STR;
import static com.dft.mom.domain.util.EntityConstants.NON_MEMBER_STR;
import static com.dft.mom.domain.validator.MemberValidator.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {

    private final LoginService loginService;
    private final RoleService roleService;
    private final FamilyService familyService;
    private final MemberService memberService;
    private final LoginRedisService loginRedisService;

    /* 카카오 로그인 */
    @PostMapping("/login/kakao")
    public void kakaoLogin() {
    }

    /* 애플 로그인 */
    @PostMapping("/login/apple")
    public void appleLogin() {
    }

    /* 비회원 토큰 발급 */
    @PostMapping("/login/non")
    public TokenResponseDto unAuthLogin() {
        String memberId = UUID.randomUUID().toString();
        return loginService.createToken(memberId, NON_MEMBER_STR);
    }

    /* 토큰 검증 */
    @GetMapping("/validate")
    public MemberStatusResponseDto validateToken(
            Authentication authentication,
            HttpServletRequest request
    ) {
        validateAuthentication(authentication, request);
        String role = roleService.getMemberRole(authentication);
        return new MemberStatusResponseDto(role);
    }

    /* 토큰 재발급 */
    @GetMapping("/reissue")
    public TokenResponseDto reIssueToken(
            Authentication authentication,
            HttpServletRequest request
    ) {
        validateAuthentication(authentication, request);
        String memberRole = roleService.getMemberRole(authentication);

        if (Objects.equals(memberRole, NON_MEMBER_STR)) {
            return loginService.createToken(
                    authentication.getName(),
                    NON_MEMBER_STR
            );
        }

        Long memberId = parseLong(authentication.getName());
        String token = getToken(request);
        loginService.validateLogin(token, memberId.toString(), REFRESH_TOKEN);
        return loginService.createToken(
                authentication.getName(),
                MEMBER_STR
        );
    }

    /* 카카오 회원 가입 */
    @PostMapping("/kakao")
    public TokenResponseDto createMember(
            @RequestBody MemberCreateRequestDto dto
    ) {
        validateCreateMember(dto);
        String socialId = loginService.accessToKakao(dto.getAccessToken());

        if (dto.getCode() != null && !dto.getCode().isEmpty()) {
            Family family = familyService.getFamilyByCode(dto.getCode());
            Member member = memberService.createMemberWithCode(
                    family,
                    dto,
                    socialId
            );
            return loginService.createToken(member);
        }

        Member member = memberService.createMember(dto, socialId);
        return loginService.createToken(member);
    }

    /* 애플 회원 가입 */
    @PostMapping("/apple")
    public TokenResponseDto createAppleMember(
            @RequestBody MemberAppleCreateRequestDto dto
    ) {
        validateCreateAppleMember(dto);
        loginService.accessToApple(dto.getIdToken(), dto.getUser());

        if (dto.getCode() != null && !dto.getCode().isEmpty()) {
            Family family = familyService.getFamilyByCode(dto.getCode());
            Member member = memberService.createAppleMemberWithCode(
                    family,
                    dto
            );
            return loginService.createToken(member);
        }

        Member member = memberService.createAppleMember(dto);
        return loginService.createToken(member);
    }

    /* 로그아웃 */
    @PostMapping("/logout")
    public void logout(
            Authentication authentication,
            HttpServletRequest request
    ) {
        validateAuthentication(authentication, request);
        Long memberId = parseLong(authentication.getName());
        loginRedisService.deleteTokenById(memberId.toString());
    }

    /* 회원탈퇴 */
    @DeleteMapping()
    public void deleteMember(
            Authentication authentication,
            HttpServletRequest request
    ) {
        validateAuthentication(authentication, request);
        Long memberId = parseLong(authentication.getName());
        Member member = memberService.getMember(memberId);
        memberService.deleteMember(member);
        loginRedisService.deleteTokenById(memberId.toString());
    }
}