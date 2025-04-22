package com.dft.mom.domain.service;

import com.dft.mom.domain.dto.member.req.MemberAppleCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberUpdateRequestDto;
import com.dft.mom.domain.dto.member.res.MemberResponseDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.domain.entity.member.Auth;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.generator.CodeGenerator;
import com.dft.mom.domain.repository.FamilyRepository;
import com.dft.mom.domain.repository.MemberRepository;
import com.dft.mom.web.exception.member.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.dft.mom.web.exception.ExceptionType.MEMBER_NOT_EXIST;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;
    private final BabyService babyService;
    private final CodeGenerator codeGenerator;

    /* 회원만 조회 */
    @Transactional(readOnly = true)
    public Member getMemberOnly(Long memberId) {
        Optional<Member> optMember = memberRepository.findById(memberId);
        if (optMember.isEmpty()) {
            throw new MemberException(
                    MEMBER_NOT_EXIST.getCode(),
                    MEMBER_NOT_EXIST.getErrorMessage()
            );
        }
        return optMember.get();
    }

    /* 회원 및 가족 조회 */
    @Transactional(readOnly = true)
    public Member getMember(Long memberId) {
        Optional<Member> optMember = memberRepository.findByIdWithFamily(memberId);
        if (optMember.isEmpty()) {
            throw new MemberException(
                    MEMBER_NOT_EXIST.getCode(),
                    MEMBER_NOT_EXIST.getErrorMessage()
            );
        }
        return optMember.get();
    }

    /* 회원 조회 */
    @Transactional(readOnly = true)
    public MemberResponseDto getMemberResponse(Long memberId) {
        Member member = getMember(memberId);
        Family family = member.getFamily();
        List<Baby> babyList = family.getBabyList();
        return new MemberResponseDto(member, family, babyList);
    }

    /* 회원 리스트 조회 */
    @Transactional(readOnly = true)
    public List<Member> getMemberListByFamilyId(Long familyId) {
        return memberRepository.findMemberListByFamilyId(familyId);
    }

    /* 카카오 회원저장 */
    @Transactional
    public Member createMember(
            MemberCreateRequestDto dto,
            String socialId
    ) {
        Member member = new Member(dto, socialId);
        new Auth(member);
        Family family = new Family(codeGenerator.generateBase32Id());
        family.addMember(member);

        Family savedFamily = familyRepository.save(family);
        Member savedMember = memberRepository.save(member);

        babyService.createParentingList(dto.getParentingList(), savedFamily);
        babyService.createPregnancyList(dto.getPregnancyList(), savedFamily);
        return savedMember;
    }

    /* 애플 회원저장 */
    @Transactional
    public Member createAppleMember(MemberAppleCreateRequestDto dto) {
        Member member = new Member(dto, dto.getUser());
        new Auth(member);
        Family family = new Family(codeGenerator.generateBase32Id());
        family.addMember(member);

        Family savedFamily = familyRepository.save(family);
        Member savedMember = memberRepository.save(member);

        babyService.createParentingList(dto.getParentingList(), savedFamily);
        babyService.createPregnancyList(dto.getPregnancyList(), savedFamily);
        return savedMember;
    }

    /* 카카오 회원저장 - 초대코드 */
    @Transactional
    public Member createMemberWithCode(
            Family family,
            MemberCreateRequestDto dto,
            String socialId
    ) {
        Member member = new Member(dto, socialId);
        new Auth(member);
        family.addMember(member);

        return memberRepository.save(member);
    }

    /* 애플 회원저장 - 초대코드 */
    @Transactional
    public Member createAppleMemberWithCode(
            Family family,
            MemberAppleCreateRequestDto dto
    ) {
        Member member = new Member(dto, dto.getUser());
        new Auth(member);
        family.addMember(member);

        return memberRepository.save(member);
    }

    /* 회원정보 업데이트 */
    public Member updateProfile(
            Member member,
            MemberUpdateRequestDto dto
    ) {
        if (dto.getName() != null) {
            member.setName(dto.getName());
        }
        if (dto.getBirth() != null) {
            member.setBirth(dto.getBirth());
        }
        if (dto.getDevice() != null) {
            member.setDevice(dto.getDevice());
        }
        if (dto.getRelation() != null) {
            member.setRelation(dto.getRelation());
        }
        if (dto.getGender() != null) {
            member.setGender(dto.getGender());
        }
        if (dto.getAlarmList() != null) {
            member.setAlarmList(dto.getAlarmList());
        }
        if (dto.getAgreeList() != null) {
            member.setAgreeList(dto.getAgreeList());
        }

        return member;
    }
}