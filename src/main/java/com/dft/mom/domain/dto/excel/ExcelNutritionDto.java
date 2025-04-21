package com.dft.mom.domain.dto.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter @Service
@AllArgsConstructor
@NoArgsConstructor
public class ExcelNutritionDto {
    private String title;
    private String summary;
    private int tag;
    private int category;
}