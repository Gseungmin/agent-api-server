package com.dft.mom.domain.dto.item.res;

import com.dft.mom.domain.entity.post.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {

    private Long id;
    private String title;
    private String content;
    private Integer version;
    private List<SubItemResponseDto> subItemList;

    public ItemResponseDto(Post item, List<SubItemResponseDto> subItemList) {
        this.id = item.getItemId();
        this.title = item.getTitle();
        this.content = item.getContent();
        this.subItemList = subItemList;
    }

    public ItemResponseDto(Nutrition item, List<SubItemResponseDto> subItemList) {
        this.id = item.getItemId();
        this.title = item.getTitle();
        this.content = item.getContent();
        this.subItemList = subItemList;
    }

    public ItemResponseDto(Inspection item, List<SubItemResponseDto> subItemList) {
        this.id = item.getItemId();
        this.title = item.getTitle();
        this.content = item.getContent();
        this.subItemList = subItemList;
    }
}
