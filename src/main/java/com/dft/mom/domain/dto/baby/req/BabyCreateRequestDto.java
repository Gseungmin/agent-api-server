package com.dft.mom.domain.dto.baby.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BabyCreateRequestDto {

    private List<PregnancyCreateRequestDto> pregnancyList;
    private List<ParentingCreateRequestDto> parentingList;
}
