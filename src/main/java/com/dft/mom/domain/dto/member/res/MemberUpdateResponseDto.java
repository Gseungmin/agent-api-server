package com.dft.mom.domain.dto.member.res;

import com.dft.mom.domain.entity.member.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateResponseDto {

    private String name;
    private String birth;
    private Integer gender;
    private Integer relation;

    public MemberUpdateResponseDto(Member member) {
        this.name = member.getName();
        if (member.getBirth() != null) {
            this.birth = member.getBirth().toString();
        }
        this.gender = member.getGender();
        this.relation = member.getRelation();
    }
}
