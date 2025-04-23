package com.dft.mom.domain.dto.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter @Service
@AllArgsConstructor
@NoArgsConstructor
public class ExcelSubItemDto {
    private String title;
    private String content;
}