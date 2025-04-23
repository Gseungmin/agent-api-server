package com.dft.mom.domain.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Getter @Service
@AllArgsConstructor
@NoArgsConstructor
public class SubItemDto {
    private Long subItemId;
    private String title;
    private String content;
    private Boolean isQna;
}