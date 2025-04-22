package com.dft.mom.domain.dto.page.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponseDto {

    private Integer category;
    private List<PostResponseDto> postList;
    private List<NutritionResponseDto> nutritionList;
    private List<InspectionResponseDto> inspectionList;
}
