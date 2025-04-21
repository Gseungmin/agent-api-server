package com.dft.mom.domain.dto.page.res;

import com.dft.mom.domain.entity.post.Nutrition;
import com.dft.mom.domain.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NutritionResponseDto {

    private Long itemId;
    private String title;
    private String content;
    private Integer priority;
    private Integer category;
    private Integer tag;

    public NutritionResponseDto(Nutrition nutrition) {
        this.itemId = nutrition.getItemId();
        this.title = nutrition.getTitle();
        this.content = nutrition.getContent();
        this.priority = nutrition.getPriority();
        this.category = nutrition.getCategory();
        this.tag = nutrition.getTag();
    }
}
