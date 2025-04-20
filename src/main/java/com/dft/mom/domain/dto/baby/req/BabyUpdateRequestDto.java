package com.dft.mom.domain.dto.baby.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BabyUpdateRequestDto {

    private Long id;
    private Long familyId;
    private String name;
    private LocalDate birth;
    private Integer birthTime;
    private Integer gender;
    private Integer type;
    private LocalDate lastMenstrual;
}
