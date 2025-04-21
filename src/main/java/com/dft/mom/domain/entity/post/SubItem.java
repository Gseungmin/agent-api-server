package com.dft.mom.domain.entity.post;

import com.dft.mom.domain.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.dft.mom.domain.util.PostConstants.NOT_QNA;
import static com.dft.mom.domain.util.PostConstants.PRIORITY_MEDIUM;

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

    public SubItem(Long itemId, String title, String content, Integer isQna) {
        this.itemId = itemId;
        this.title = title;
        this.content = content;
        this.isQna = isQna;
    }

    public void addPost(Post post) {
        this.post = post;
        post.getSubItemList().add(this);
    }

    public void addInspection(Inspection inspection) {
        this.inspection = inspection;
        inspection.getSubItemList().add(this);
    }

    public void updateItem(String title, String content, Integer isQna, Integer priority) {
        this.title = title;
        this.content = content;
        this.isQna = isQna;
        this.priority = priority;
    }
}
