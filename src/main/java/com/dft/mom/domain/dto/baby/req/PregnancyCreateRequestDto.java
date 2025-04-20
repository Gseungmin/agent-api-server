package com.dft.mom.domain.dto.baby.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class PregnancyCreateRequestDto {

    private String name;
    private LocalDate expectedBirth;
    private LocalDate lastMenstrual;
}
