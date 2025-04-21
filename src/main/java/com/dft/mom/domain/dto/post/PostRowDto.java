package com.dft.mom.domain.dto.post;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRowDto {

    private Long itemId;
    private String title;
    private String summary;
    private Integer type;
    private Integer startPeriod;
    private Integer endPeriod;
    private Integer category;
    private Boolean caution;
}
