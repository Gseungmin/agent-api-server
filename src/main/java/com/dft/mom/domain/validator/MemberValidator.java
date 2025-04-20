package com.dft.mom.domain.validator;

import com.dft.mom.domain.dto.baby.req.BabyTypeUpdateRequestDto;
import com.dft.mom.domain.dto.baby.req.BabyUpdateRequestDto;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberAppleCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberUpdateRequestDto;
import com.dft.mom.web.exception.ExceptionType;
import com.dft.mom.web.exception.member.FamilyException;
import com.dft.mom.web.exception.member.MemberException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static com.dft.mom.domain.util.EntityConstants.*;
import static com.dft.mom.domain.validator.CommonValidator.validateId;
import static com.dft.mom.web.exception.ExceptionType.*;

public class MemberValidator {
    public static void validateAuthentication(Authentication authentication, HttpServletRequest request) {
        if (authentication == null) {
            ExceptionType exception = (ExceptionType) request.getAttribute("exception");
            throw new MemberException(exception.getCode(), exception.getErrorMessage());
        }

        if (null == authentication.getName()) {
            throw new MemberException(TOKEN_INVALID.getCode(), TOKEN_INVALID.getErrorMessage());
        }
    }

    public static void validateCreateMember(MemberCreateRequestDto dto) {
        if (dto.getAccessToken() == null || dto.getAccessToken().isEmpty()) {
            throw new MemberException(SOCIAL_TOKEN_NEED.getCode(), SOCIAL_TOKEN_NEED.getErrorMessage());
        }

        validateRelation(dto.getRelation());
        validateAgree(dto.getAgree());
        validateName(dto.getName());

        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            validatePregnancyAndParentingLists(dto.getPregnancyList(), dto.getParentingList());
        }
    }

    public static void validateCreateAppleMember(MemberAppleCreateRequestDto dto) {
        if (dto.getIdToken() == null) {
            throw new MemberException(SOCIAL_TOKEN_NEED.getCode(), SOCIAL_TOKEN_NEED.getErrorMessage());
        }

        validateRelation(dto.getRelation());
        validateAgree(dto.getAgree());
        validateName(dto.getName());

        if (dto.getCode() == null || dto.getCode().trim().isEmpty()) {
            validatePregnancyAndParentingLists(dto.getPregnancyList(), dto.getParentingList());
        }
    }

    public static void validateRelation(Integer relation) {
        if (relation == null || (!relation.equals(FEMALE) && !relation.equals(MALE) && !relation.equals(FAMILY_ETC))) {
            throw new MemberException(MEMBER_CREATE_RELATION_INVALID.getCode(), MEMBER_CREATE_RELATION_INVALID.getErrorMessage());
        }
    }

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > MAX_NAME) {
            throw new MemberException(MEMBER_CREATE_NAME_INVALID.getCode(), MEMBER_CREATE_NAME_INVALID.getErrorMessage());
        }
    }

    public static boolean validateBirth(LocalDate birthDate) {
        if (birthDate == null) {
            return true;
        }

        if (birthDate.isAfter(LocalDate.now())) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate fourteenYearsAgo = today.minusYears(14);

        return !birthDate.isAfter(fourteenYearsAgo);
    }

    public static void validateAgree(Integer agree) {
        if (agree == null || (!agree.equals(AGREE_ALL) && !agree.equals(AGREE_ALL_WITH_MARKETING))) {
            throw new MemberException(MEMBER_CREATE_AGREE_INVALID.getCode(), MEMBER_CREATE_AGREE_INVALID.getErrorMessage());
        }
    }

    public static void validatePregnancyAndParentingLists(List<PregnancyCreateRequestDto> pregnancyList,
                                                          List<ParentingCreateRequestDto> parentingList) {
        boolean hasPregnancy = pregnancyList != null && !pregnancyList.isEmpty();
        boolean hasBaby = parentingList != null && !parentingList.isEmpty();

        if (hasPregnancy == hasBaby) {
            throw new MemberException(MEMBER_CREATE_BABY_LIST_INVALID.getCode(), MEMBER_CREATE_BABY_LIST_INVALID.getErrorMessage());
        }

        if (hasPregnancy) {
            validatePregnancyList(pregnancyList);
        }

        if (hasBaby) {
            validateParentingList(parentingList);
        }
    }

    public static void validatePregnancyList(List<PregnancyCreateRequestDto> pregnancyList) {
        if (pregnancyList.size() > MAX_BABY_CREATE) {
            throw new MemberException(MEMBER_CREATE_MAX_BABY_LIST_INVALID.getCode(), MEMBER_CREATE_MAX_BABY_LIST_INVALID.getErrorMessage());
        }

        for (PregnancyCreateRequestDto pregnancy : pregnancyList) {
            validatePregnancyItem(pregnancy);
        }
    }

    private static void validateParentingList(List<ParentingCreateRequestDto> babyList) {
        if (babyList.size() > MAX_BABY_CREATE) {
            throw new MemberException(MEMBER_CREATE_MAX_BABY_LIST_INVALID.getCode(), MEMBER_CREATE_MAX_BABY_LIST_INVALID.getErrorMessage());
        }

        for (ParentingCreateRequestDto baby : babyList) {
            validateBabyItem(baby);
        }
    }

    private static void validatePregnancyItem(PregnancyCreateRequestDto pregnancy) {
        if (pregnancy.getName() == null || pregnancy.getName().isEmpty() || pregnancy.getName().length() > MAX_NAME) {
            throw new MemberException(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), MEMBER_CREATE_BABY_INFO_INVALID.getErrorMessage());
        }

        if (pregnancy.getExpectedBirth() == null && pregnancy.getLastMenstrual() == null) {
            throw new MemberException(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), MEMBER_CREATE_BABY_INFO_INVALID.getErrorMessage());
        }

        if (pregnancy.getExpectedBirth() != null && pregnancy.getExpectedBirth().isBefore(LocalDate.now())) {
            throw new MemberException(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), MEMBER_CREATE_BABY_INFO_INVALID.getErrorMessage());
        }

        if (pregnancy.getLastMenstrual() != null && pregnancy.getLastMenstrual().isAfter(LocalDate.now())) {
            throw new MemberException(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), MEMBER_CREATE_BABY_INFO_INVALID.getErrorMessage());
        }
    }

    private static void validateBabyItem(ParentingCreateRequestDto baby) {
        if (baby.getName() == null || baby.getName().isEmpty() || baby.getName().length() > MAX_NAME) {
            throw new MemberException(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), MEMBER_CREATE_BABY_INFO_INVALID.getErrorMessage());
        }

        if (baby.getBirth() == null || baby.getBirth().isAfter(LocalDate.now())) {
            throw new MemberException(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), MEMBER_CREATE_BABY_INFO_INVALID.getErrorMessage());
        }

        if (baby.getGender() == null || (!baby.getGender().equals(FEMALE) && !baby.getGender().equals(MALE))) {
            throw new MemberException(MEMBER_CREATE_BABY_INFO_INVALID.getCode(), MEMBER_CREATE_BABY_INFO_INVALID.getErrorMessage());
        }
    }

    public static void validateUpdateMember(MemberUpdateRequestDto dto) {
        if (dto.getRelation() != null) {
            validateRelation(dto.getRelation());
        }

        if (dto.getAgreeList() != null) {
            validateAgree(dto.getAgreeList());
        }

        if (dto.getBirth() != null) {
            boolean isValidBirth = validateBirth(dto.getBirth());
            if (!isValidBirth) {
                throw new MemberException(MEMBER_UPDATE_BIRTH_INVALID.getCode(), MEMBER_UPDATE_BIRTH_INVALID.getErrorMessage());
            }
        }

        if (dto.getName() != null && (dto.getName().isEmpty() || dto.getName().length() > MAX_NAME)) {
            throw new MemberException(MEMBER_UPDATE_NAME_INVALID.getCode(), MEMBER_UPDATE_NAME_INVALID.getErrorMessage());
        }

        if (dto.getGender() != null && (!dto.getGender().equals(FEMALE) && !dto.getGender().equals(MALE))) {
            throw new MemberException(MEMBER_UPDATE_GENDER_INVALID.getCode(), MEMBER_UPDATE_GENDER_INVALID.getErrorMessage());
        }

        if (dto.getAlarmList() != null && dto.getAlarmList() < 0) {
            throw new MemberException(MEMBER_UPDATE_ALARM_LIST_INVALID.getCode(), MEMBER_UPDATE_ALARM_LIST_INVALID.getErrorMessage());
        }
    }

    public static void validateUpdateBaby(BabyUpdateRequestDto dto) {
        validateId(dto.getId());
        validateId(dto.getFamilyId());

        if (dto.getLastMenstrual() != null && dto.getLastMenstrual().isAfter(LocalDate.now())) {
            throw new FamilyException(BABY_UPDATE_BIRTH_INVALID.getCode(), BABY_UPDATE_BIRTH_INVALID.getErrorMessage());
        }

        if (dto.getBirthTime() != null && (dto.getBirthTime() < MIN_TIME || dto.getBirthTime() >= MAX_TIME)) {
            throw new FamilyException(BABY_UPDATE_BIRTH_TIME_INVALID.getCode(), BABY_UPDATE_BIRTH_TIME_INVALID.getErrorMessage());
        }

        if (dto.getBirth() != null) {
            if (Objects.equals(dto.getType(), PREGNANT) && dto.getBirth().isBefore(LocalDate.now())) {
                throw new FamilyException(BABY_UPDATE_BIRTH_INVALID.getCode(), BABY_UPDATE_BIRTH_INVALID.getErrorMessage());
            }

            if (Objects.equals(dto.getType(), BABY) && dto.getBirth().isAfter(LocalDate.now())) {
                throw new FamilyException(BABY_UPDATE_BIRTH_INVALID.getCode(), BABY_UPDATE_BIRTH_INVALID.getErrorMessage());
            }
        }

        if (dto.getName() != null && (dto.getName().isEmpty() || dto.getName().length() > MAX_NAME)) {
            throw new FamilyException(BABY_UPDATE_NAME_INVALID.getCode(), BABY_UPDATE_NAME_INVALID.getErrorMessage());
        }

        if (dto.getGender() != null && (!dto.getGender().equals(FEMALE) && !dto.getGender().equals(MALE))) {
            throw new FamilyException(BABY_UPDATE_GENDER_INVALID.getCode(), BABY_UPDATE_GENDER_INVALID.getErrorMessage());
        }
    }

    public static void validateUpdateBabyType(BabyTypeUpdateRequestDto dto) {
        validateId(dto.getId());
        validateId(dto.getFamilyId());

        if (dto.getType() == null || dto.getType() < 0 || dto.getType() > 1) {
            throw new FamilyException(BABY_UPDATE_TYPE_INVALID.getCode(), BABY_UPDATE_TYPE_INVALID.getErrorMessage());
        }
    }
}
