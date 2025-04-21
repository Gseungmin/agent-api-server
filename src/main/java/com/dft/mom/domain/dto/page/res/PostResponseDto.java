package com.dft.mom.domain.dto.page.res;

import com.dft.mom.domain.entity.post.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostResponseDto {

    private Long itemId;
    private String title;
    private String content;
    private Integer priority;
    private Integer category;

    public PostResponseDto(Post post) {
        this.itemId = post.getItemId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.priority = post.getPriority();
        this.category = post.getCategory();
    }
}
