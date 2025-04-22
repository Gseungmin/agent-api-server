package com.dft.mom.domain.entity.post;

import com.dft.mom.domain.dto.post.NutritionRowDto;
import com.dft.mom.domain.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.dft.mom.domain.util.PostConstants.PRIORITY_MEDIUM;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Nutrition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nutritionId")
    private Long id;

    @Column(name = "itemId", nullable = false, unique = true)
    private Long itemId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private Integer tag;

    @Column(nullable = false)
    private Integer category;

    @Column(nullable = false)
    private Integer priority = PRIORITY_MEDIUM;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nutrition")
    private List<BabyPageItem> babyPageItemList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "nutrition")
    private List<SubItem> subItemList = new ArrayList<>();

    public Nutrition(NutritionRowDto data) {
        this.itemId = data.getItemId();
        this.tag = data.getTag();
        this.title = data.getTitle();
        this.content = data.getSummary();
        this.category = data.getCategory();
    }

    public void updateNutrition(NutritionRowDto data) {
        this.tag = data.getTag();
        this.title = data.getTitle();
        this.content = data.getSummary();
        this.category = data.getCategory();
    }
}
