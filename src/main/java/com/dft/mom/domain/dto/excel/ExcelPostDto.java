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
public class ExcelPostDto {
    private String title;
    private String summary;
    private int type;
    private int start_period;
    private int end_period;
    private int category;
    private boolean caution;

    @JsonProperty("subject_list")
    private List<ExcelSubItemDto> subjectList;
}