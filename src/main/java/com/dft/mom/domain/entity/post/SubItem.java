package com.dft.mom.domain.entity.post;

import com.dft.mom.domain.dto.post.SubItemDto;
import com.dft.mom.domain.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.dft.mom.domain.util.EntityConstants.DEFAULT_PAGE_VERSION;
import static com.dft.mom.domain.util.PostConstants.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subItemId")
    private Long id;

    @Column(name = "itemId", nullable = false, unique = true)
    private Long itemId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer isQna = NOT_QNA;

    @Column(nullable = false)
    private Integer priority = PRIORITY_MEDIUM;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutritionId")
    private Nutrition nutrition;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspectionId")
    private Inspection inspection;

    public void updateSubItem(SubItemDto data) {
        this.title = data.getTitle();
        this.content = data.getContent();
        this.isQna = data.getIsQna() ? QNA : NOT_QNA;
    }

    public SubItem(SubItemDto data, Post post) {
        this.itemId = data.getSubItemId();
        this.title = data.getTitle();
        this.content = data.getContent();
        this.isQna = data.getIsQna() ? QNA : NOT_QNA;
        addPost(post);
    }

    public SubItem(SubItemDto data, Inspection inspection) {
        this.itemId = data.getSubItemId();
        this.title = data.getTitle();
        this.content = data.getContent();
        this.isQna = data.getIsQna() ? QNA : NOT_QNA;
        addInspection(inspection);
    }

    public SubItem(SubItemDto data, Nutrition nutrition) {
        this.itemId = data.getSubItemId();
        this.title = data.getTitle();
        this.content = data.getContent();
        this.isQna = data.getIsQna() ? QNA : NOT_QNA;
        addNutrition(nutrition);
    }

    public void addPost(Post post) {
        this.post = post;
        post.getSubItemList().add(this);
    }

    public void addInspection(Inspection inspection) {
        this.inspection = inspection;
        inspection.getSubItemList().add(this);
    }

    public void addNutrition(Nutrition nutrition) {
        this.nutrition = nutrition;
        nutrition.getSubItemList().add(this);
    }
}
