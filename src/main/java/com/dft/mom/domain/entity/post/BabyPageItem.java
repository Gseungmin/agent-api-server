package com.dft.mom.domain.entity.post;

import com.dft.mom.domain.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BabyPageItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "babyPageItemId")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "babyPageId")
    private BabyPage babyPage;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postId")
    private Post post;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspectionId")
    private Inspection inspection;

    public BabyPageItem(BabyPage babyPage, Post post) {
        this.babyPage = babyPage;
        this.post = post;
        post.getBabyPageItemList().add(this);
        babyPage.getBabyPageItemList().add(this);
    }

    public BabyPageItem(BabyPage babyPage, Inspection inspection) {
        this.babyPage = babyPage;
        this.inspection = inspection;
        inspection.getBabyPageItemList().add(this);
        babyPage.getBabyPageItemList().add(this);
    }

    public void initBabyPageItem() {
        this.babyPage = null;
        this.post = null;
        this.inspection = null;
    }
}
