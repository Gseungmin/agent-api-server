package com.dft.mom.web.controller;

import com.dft.mom.domain.dto.baby.req.BabyCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.BabyDeleteRequestDto;
import com.dft.mom.domain.dto.baby.req.BabyTypeUpdateRequestDto;
import com.dft.mom.domain.dto.baby.req.BabyUpdateRequestDto;
import com.dft.mom.domain.dto.baby.res.BabyResponseDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.service.BabyService;
import com.dft.mom.domain.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.dft.mom.domain.function.FunctionUtil.parseLong;
import static com.dft.mom.domain.validator.CommonValidator.validateId;
import static com.dft.mom.domain.validator.MemberValidator.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/baby")
public class BabyController {

    private final MemberService memberService;
    private final BabyService babyService;

    /*아이정보 추가*/
    @PostMapping
    public List<BabyResponseDto> createBaby(
            Authentication authentication,
            HttpServletRequest request,
            @RequestBody BabyCreateRequestDto dto
    ) {
        validateAuthentication(authentication, request);
        validatePregnancyAndParentingLists(dto.getPregnancyList(), dto.getParentingList());

        Long memberId = Long.parseLong(authentication.getName());
        Member member = memberService.getMember(memberId);
        return babyService.createBaby(member, dto);
    }

    /*아이정보 업데이트*/
    @PatchMapping
    public BabyResponseDto updateBaby(
            Authentication authentication,
            HttpServletRequest request,
            @RequestBody BabyUpdateRequestDto dto
    ) {
        validateAuthentication(authentication, request);
        validateUpdateBaby(dto);

        Baby baby = babyService.getBaby(dto.getId());
        return babyService.updateBaby(baby, dto);
    }

    /*아이 타입 정보 업데이트 - 임신중에서 육아중으로 변경*/
    @PatchMapping("/type")
    public BabyResponseDto updateBabyType(
            Authentication authentication,
            HttpServletRequest request,
            @RequestBody BabyTypeUpdateRequestDto dto
    ) {
        validateAuthentication(authentication, request);
        validateUpdateBabyType(dto);

        Baby baby = babyService.getBaby(dto.getId());
        return babyService.updateBabyType(baby, dto, LocalDate.now());
    }

    /*아이정보 삭제*/
    @DeleteMapping
    public void deleteBaby(
            Authentication authentication,
            HttpServletRequest request,
            @RequestBody BabyDeleteRequestDto dto
    ) {
        validateAuthentication(authentication, request);
        validateId(dto.getId());
        validateId(dto.getFamilyId());

        Baby baby = babyService.getBaby(dto.getId());
        babyService.deleteBaby(baby, dto.getFamilyId());
    }
}