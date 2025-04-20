package com.dft.mom.domain.dto.member.req;

import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberAppleCreateRequestDto {

    private String idToken;
    private String user;
    private String code;
    private String name;
    private Integer relation;
    private Integer agree;

    private List<PregnancyCreateRequestDto> pregnancyList;
    private List<ParentingCreateRequestDto> parentingList;
}
