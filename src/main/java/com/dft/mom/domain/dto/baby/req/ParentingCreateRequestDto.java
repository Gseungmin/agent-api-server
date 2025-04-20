package com.dft.mom.domain.dto.baby.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class ParentingCreateRequestDto {

    private String name;
    private LocalDate birth;
    private Integer gender;
}
