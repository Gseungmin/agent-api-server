package com.dft.mom.web.controller;

import com.dft.mom.domain.dto.member.req.MemberUpdateRequestDto;
import com.dft.mom.domain.dto.member.res.MemberResponseDto;
import com.dft.mom.domain.dto.member.res.MemberUpdateResponseDto;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.service.MemberService;
import com.dft.mom.domain.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.dft.mom.domain.function.FunctionUtil.parseLong;
import static com.dft.mom.domain.validator.MemberValidator.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/my")
public class MyController {

    private final MemberService memberService;
    private final RoleService roleService;

    /*회원 조회*/
    @GetMapping()
    public MemberResponseDto getMember(Authentication authentication, HttpServletRequest request) {
        validateAuthentication(authentication, request);

        String role = roleService.getMemberRole(authentication);
        roleService.validateNon(role);

        Long memberId = parseLong(authentication.getName());
        MemberResponseDto response = memberService.getMemberResponse(memberId);
        response.setRole(role);
        return response;
    }

    /*프로필 업데이트*/
    @PatchMapping()
    public MemberUpdateResponseDto updateProfile(Authentication authentication, HttpServletRequest request, @RequestBody MemberUpdateRequestDto dto) {
        validateAuthentication(authentication, request);
        validateUpdateMember(dto);

        Long memberId = parseLong(authentication.getName());
        Member member = memberService.getMemberOnly(memberId);
        Member savedMember = memberService.updateProfile(member, dto);
        return new MemberUpdateResponseDto(savedMember);
    }
}
