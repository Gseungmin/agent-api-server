package com.dft.mom;


import com.dft.mom.domain.dto.baby.req.BabyUpdateRequestDto;
import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.dft.mom.domain.util.EntityConstants.*;

public class CreateUtil {

    public static MemberCreateRequestDto createMemberCreateRequestDto(String code,
                                                                      List<PregnancyCreateRequestDto> pregnancyList,
                                                                      List<ParentingCreateRequestDto> babyList) {
        return new MemberCreateRequestDto("토큰", code, "김마미", FEMALE, AGREE_ALL, pregnancyList, babyList);
    }

    public static PregnancyCreateRequestDto createPregnancyCreateRequestDto(String name,
                                                                            LocalDate expectedBirth,
                                                                            LocalDate lastMenstrual) {
        return new PregnancyCreateRequestDto(name, expectedBirth, lastMenstrual);
    }

    public static List<PregnancyCreateRequestDto> createPregnancyListCreateRequestDto(LocalDate expectedBirth,
                                                                                      LocalDate lastMenstrual,
                                                                                      int count) {

        List<PregnancyCreateRequestDto> pregnancyList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String name = "임신중" + (i + 1);
            PregnancyCreateRequestDto pregnancy = CreateUtil.createPregnancyCreateRequestDto(
                    name,
                    expectedBirth,
                    lastMenstrual
            );
            pregnancyList.add(pregnancy);
        }

        return pregnancyList;
    }

    public static ParentingCreateRequestDto createParentingCreateRequestDto(String name, LocalDate birth) {
        return new ParentingCreateRequestDto(name, birth, FEMALE);
    }

    public static BabyUpdateRequestDto createBabyUpdateRequestDto(Long babyId, Long familyId, String name) {
        return new BabyUpdateRequestDto(babyId, familyId, name, LocalDate.now().minusDays(20), 0,
                MALE, PREGNANT, LocalDate.now().minusDays(20));
    }
}
