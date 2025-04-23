package com.dft.mom.domain.dto.item.res;

import com.dft.mom.domain.entity.post.SubItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubItemResponseDto {

    private Long itemId;
    private String title;
    private String content;
    private Integer isQna;
    private Integer priority;

    public SubItemResponseDto(SubItem subItem) {
        this.itemId = subItem.getItemId();
        this.title = subItem.getTitle();
        this.content = subItem.getContent();
        this.isQna = subItem.getIsQna();
        this.priority = subItem.getPriority();
    }
}
