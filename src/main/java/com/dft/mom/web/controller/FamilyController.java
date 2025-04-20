package com.dft.mom.web.controller;

import com.dft.mom.domain.dto.family.req.FamilyConnectRequestDto;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.service.FamilyService;
import com.dft.mom.domain.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.dft.mom.domain.function.FunctionUtil.parseLong;
import static com.dft.mom.domain.validator.MemberValidator.validateAuthentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/family")
public class FamilyController {

    private final MemberService memberService;
    private final FamilyService familyService;

    /*가족 재연결*/
    @PostMapping()
    public void connectFamily(Authentication authentication, HttpServletRequest request,
                                          @RequestBody FamilyConnectRequestDto dto) {
        validateAuthentication(authentication, request);
        Long memberId = parseLong(authentication.getName());

        Member invitee = memberService.getMember(memberId);
        Family inviterFamily = familyService.getFamilyByCode(dto.getCode());
        familyService.connectFamily(invitee, inviterFamily);
    }

    /*가족 연결 해제*/
    @DeleteMapping()
    public Member disconnectFamily(Authentication authentication, HttpServletRequest request) {
        validateAuthentication(authentication, request);
        Long memberId = parseLong(authentication.getName());
        Member member = memberService.getMember(memberId);
        return familyService.disConnectFamily(member);
    }
}
