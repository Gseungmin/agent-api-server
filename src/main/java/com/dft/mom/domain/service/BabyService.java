package com.dft.mom.domain.service;

import com.dft.mom.domain.dto.baby.req.*;
import com.dft.mom.domain.dto.baby.res.BabyResponseDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.repository.BabyRepository;
import com.dft.mom.web.exception.member.FamilyException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.dft.mom.web.exception.ExceptionType.BABY_NOT_EXIST;
import static com.dft.mom.web.exception.ExceptionType.UN_AUTH_BABY;

@Service
@RequiredArgsConstructor
@Transactional
public class BabyService {

    private final BabyRepository babyRepository;

    /* 아이정보 조회 */
    @Transactional(readOnly = true)
    public Baby getBaby(Long babyId) {
        Optional<Baby> optBaby = babyRepository.findBabyById(babyId);

        if (optBaby.isEmpty()) {
            throw new FamilyException(BABY_NOT_EXIST.getCode(), BABY_NOT_EXIST.getErrorMessage());
        }

        return optBaby.get();
    }

    /* 아이리스트 조회 */
    @Transactional(readOnly = true)
    public List<BabyResponseDto> getBabyList(Long familyId) {
        List<Baby> babyList = babyRepository.findBabyListByFamilyId(familyId);
        return babyList.stream().map(BabyResponseDto::new).toList();
    }

    /* 아이저장 */
    public List<BabyResponseDto> createBaby(Member member, BabyCreateRequestDto dto) {
        Family family = member.getFamily();

        List<Baby> pregnancyList = createPregnancyList(dto.getPregnancyList(), family);
        List<Baby> parentingList = createParentingList(dto.getParentingList(), family);

        pregnancyList.addAll(parentingList);
        return pregnancyList.stream().map(BabyResponseDto::new).toList();
    }

    /* 아이저장 */
    public List<Baby> createParentingList(List<ParentingCreateRequestDto> list, Family family) {
        if (list == null) {
            return new ArrayList<>();
        }

        List<Baby> babyList = new ArrayList<>();

        for (ParentingCreateRequestDto dto : list) {
            Baby baby = new Baby(dto);
            baby.addFamily(family);
            babyList.add(baby);
        }

        return babyRepository.saveAll(babyList);
    }

    /* 태아저장 */
    public List<Baby> createPregnancyList(List<PregnancyCreateRequestDto> list, Family family) {
        if (list == null) {
            return new ArrayList<>();
        }

        List<Baby> babyList = new ArrayList<>();

        for (PregnancyCreateRequestDto dto : list) {
            Baby baby = new Baby(dto);
            baby.addFamily(family);
            babyList.add(baby);
        }

        return babyRepository.saveAll(babyList);
    }

    /* 아이수정 */
    public BabyResponseDto updateBaby(Baby baby, BabyUpdateRequestDto dto) {
        if (!Objects.equals(dto.getFamilyId(), baby.getFamily().getId())) {
            throw new FamilyException(UN_AUTH_BABY.getCode(), UN_AUTH_BABY.getErrorMessage());
        }

        if (dto.getName() != null) {
            baby.setName(dto.getName());
        }

        if (dto.getBirth() != null) {
            baby.setBirth(dto.getBirth());
        }

        if (dto.getBirthTime() != null) {
            baby.setBirthTime(dto.getBirthTime());
        }

        if (dto.getType() != null && !dto.getType().equals(baby.getType())) {
            baby.setType(dto.getType());
        }

        if (dto.getGender() != null) {
            baby.setGender(dto.getGender());
        }

        if (dto.getLastMenstrual() != null) {
            baby.setLastMoonDate(dto.getLastMenstrual());
        }

        return new BabyResponseDto(baby);
    }

    /* 아이수정 - 태아에서 아이로 변경 */
    public BabyResponseDto updateBabyType(Baby baby, BabyTypeUpdateRequestDto dto, LocalDate today) {
        if (!Objects.equals(dto.getFamilyId(), baby.getFamily().getId())) {
            throw new FamilyException(UN_AUTH_BABY.getCode(), UN_AUTH_BABY.getErrorMessage());
        }

        if (dto.getType() != null && !dto.getType().equals(baby.getType())) {
            baby.setType(dto.getType());
            baby.setBirth(today);
        }

        return new BabyResponseDto(baby);
    }

    /* 아이삭제 */
    public void deleteBaby(Baby baby, Long familyId) {
        if (baby.getFamily() == null) {
            return;
        }

        if (!Objects.equals(baby.getFamily().getId(), familyId)) {
            throw new FamilyException(UN_AUTH_BABY.getCode(), UN_AUTH_BABY.getErrorMessage());
        }

        baby.setFamily(null);
    }
}