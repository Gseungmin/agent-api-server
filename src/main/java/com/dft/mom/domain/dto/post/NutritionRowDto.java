package com.dft.mom.domain.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionRowDto {

    private Long itemId;
    private String title;
    private String summary;
    private Integer tag;
    private Integer category;
}
