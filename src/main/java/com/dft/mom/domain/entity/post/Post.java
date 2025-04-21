package com.dft.mom.domain.entity.post;

import com.dft.mom.domain.dto.baby.post.PostRowDto;
import com.dft.mom.domain.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.dft.mom.domain.util.PostConstants.DEFAULT_POST;
import static com.dft.mom.domain.util.PostConstants.PRIORITY_MEDIUM;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "postId")
    private Long id;

    @Column(name = "itemId", nullable = false, unique = true)
    private Long itemId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private Integer category;

    @Column(nullable = false)
    private Integer type = DEFAULT_POST;

    @Column(nullable = false)
    private Integer priority = PRIORITY_MEDIUM;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private List<BabyPageItem> babyPageItemList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "post")
    private List<SubItem> subItemList = new ArrayList<>();

    public Post(PostRowDto data) {
        this.itemId = data.getItemId();
        this.title = data.getTitle();
        this.content = data.getSummary();
        this.type = data.getType();
        this.category = data.getCategory();
    }

    public void updatePost(PostRowDto data) {
        this.title = data.getTitle();
        this.content = data.getSummary();
        this.type = data.getType();
        this.category = data.getCategory();
    }
}
