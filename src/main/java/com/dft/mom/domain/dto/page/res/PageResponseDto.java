package com.dft.mom.domain.dto.page.res;

import com.dft.mom.domain.entity.post.BabyPage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResponseDto {

    private Integer pageType;
    private Integer pagePeriod;
    private List<CategoryResponseDto> categoryList;

    public PageResponseDto(BabyPage babyPage, List<CategoryResponseDto> categoryList) {
        this.pagePeriod = babyPage.getPeriod();
        this.pageType = babyPage.getType();
        this.categoryList = categoryList.stream().sorted(Comparator.comparing(CategoryResponseDto::getCategory)).toList();
    }
}
