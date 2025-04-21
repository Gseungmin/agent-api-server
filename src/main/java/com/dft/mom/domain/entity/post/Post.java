package com.dft.mom.domain.entity.post;

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

    @Column(nullable = false)
    private String content;

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

    public Post(Long itemId, String title, String summary, Integer type) {
        this.itemId = itemId;
        this.title = title;
        this.content = summary;
        this.type = type;
    }

    public void updatePost(String title, String summary, Integer type) {
        this.title = title;
        this.content = summary;
        this.type = type;
    }
}
