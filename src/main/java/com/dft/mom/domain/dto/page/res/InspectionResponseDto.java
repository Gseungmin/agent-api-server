package com.dft.mom.domain.dto.page.res;

import com.dft.mom.domain.entity.post.Inspection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InspectionResponseDto {

    private Long itemId;
    private String title;
    private String content;
    private Integer priority;
    private Integer category;
    private Integer start;
    private Integer end;

    public InspectionResponseDto(Inspection inspection) {
        this.itemId = inspection.getItemId();
        this.title = inspection.getTitle();
        this.content = inspection.getContent();
        this.priority = inspection.getPriority();
        this.category = inspection.getCategory();
        this.start = inspection.getStart();
        this.end = inspection.getEnd();
    }
}
