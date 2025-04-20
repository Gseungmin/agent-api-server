package com.dft.mom.domain.dto.baby.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BabyTypeUpdateRequestDto {

    private Long id;
    private Long familyId;
    private Integer type;
}
