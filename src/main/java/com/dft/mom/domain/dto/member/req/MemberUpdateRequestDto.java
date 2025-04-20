package com.dft.mom.domain.dto.member.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MemberUpdateRequestDto {

    private String name;
    private Integer gender;
    private Integer relation;
    private LocalDate birth;

    private String device;
    private Integer agreeList;
    private Integer alarmList;
}
