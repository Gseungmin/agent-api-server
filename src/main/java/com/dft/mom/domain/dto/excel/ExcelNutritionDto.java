package com.dft.mom.domain.dto.excel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter @Service
@AllArgsConstructor
@NoArgsConstructor
public class ExcelNutritionDto {
    private String title;
    private String summary;
    private int tag;
    private int category;

    @JsonProperty("subject_list")
    private List<ExcelSubItemDto> subjectList;
}