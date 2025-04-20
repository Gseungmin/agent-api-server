package com.dft.mom.domain.dto.member.res;

import com.dft.mom.domain.dto.baby.res.BabyResponseDto;
import com.dft.mom.domain.entity.family.Baby;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponseDto {

    private String familyId;
    private String name;
    private String birth;
    private String code;
    private Integer gender;
    private Integer relation;
    private Integer alarmList;
    private String role;

    private List<BabyResponseDto> babyList;

    public MemberResponseDto(Member member, Family family, List<Baby> babyList) {
        this.familyId = family.getId().toString();
        this.code = family.getCode();
        this.name = member.getName();
        if (member.getBirth() != null) {
            this.birth = member.getBirth().toString();
        }
        this.gender = member.getGender();
        this.relation = member.getRelation();
        this.alarmList = member.getAlarmList();
        this.babyList = babyList.stream().map(BabyResponseDto::new).toList();
    }
}
