package com.dft.mom.domain.service;

import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.generator.CodeGenerator;
import com.dft.mom.domain.repository.BabyRepository;
import com.dft.mom.domain.repository.FamilyRepository;
import com.dft.mom.domain.repository.MemberRepository;
import com.dft.mom.web.exception.member.FamilyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static com.dft.mom.domain.util.EntityConstants.FEMALE;
import static com.dft.mom.web.exception.ExceptionType.ALREADY_CONNECTED_FAMILY;
import static com.dft.mom.web.exception.ExceptionType.FAMILY_CODE_INVALID;

@Service
@RequiredArgsConstructor
@Transactional
public class FamilyService {

    private final FamilyRepository familyRepository;
    private final MemberRepository memberRepository;
    private final BabyRepository babyRepository;
    private final CodeGenerator codeGenerator;

    /* 회원 및 가족 조회 */
    @Transactional(readOnly = true)
    public Family getFamilyByCode(String code) {
        Optional<Family> optFamily = familyRepository.findFamilyByCode(code);

        if (optFamily.isEmpty()) {
            throw new FamilyException(
                    FAMILY_CODE_INVALID.getCode(),
                    FAMILY_CODE_INVALID.getErrorMessage()
            );
        }

        return optFamily.get();
    }

    /* 가족 연결 */
    public void connectFamily(Member invitee, Family inviterfamily) {
        Long inviteeFamilyId = invitee.getFamily().getId();

        if (Objects.equals(inviteeFamilyId, inviterfamily.getId())) {
            throw new FamilyException(
                    ALREADY_CONNECTED_FAMILY.getCode(),
                    ALREADY_CONNECTED_FAMILY.getErrorMessage()
            );
        }

        inviterfamily.addMember(invitee);
    }

    /* 가족 연결 해제 */
    public Member disConnectFamily(Member member) {
        Family family = new Family(codeGenerator.generateBase32Id());
        family.addMember(member);

        familyRepository.save(family);

        ParentingCreateRequestDto dto =
                new ParentingCreateRequestDto("김둥이", LocalDate.now(), FEMALE);
        Baby baby = new Baby(dto);
        baby.addFamily(family);
        babyRepository.save(baby);

        return memberRepository.save(member);
    }
}
