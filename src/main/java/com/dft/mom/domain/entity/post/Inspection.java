package com.dft.mom.domain.entity.post;

import com.dft.mom.domain.dto.post.InspectionRowDto;
import com.dft.mom.domain.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static com.dft.mom.domain.util.PostConstants.DEFAULT_IMPORTANT;
import static com.dft.mom.domain.util.PostConstants.INSPECTION_AND_VACCINATIONS;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inspection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inspectionId")
    private Long id;

    @Column(name = "itemId", nullable = false, unique = true)
    private Long itemId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer start;

    @Column(nullable = false)
    private Integer end;

    @Column(nullable = false)
    private Integer category = INSPECTION_AND_VACCINATIONS;

    @Column(nullable = false)
    private Integer priority = DEFAULT_IMPORTANT;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inspection")
    private List<BabyPageItem> babyPageItemList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inspection")
    private List<SubItem> subItemList = new ArrayList<>();

    public Inspection(InspectionRowDto data) {
        this.itemId = data.getItemId();
        this.title = data.getTitle();
        this.content = data.getSummary();
        this.start = data.getStart();
        this.end = data.getEnd();
    }

    public void updateInspection(InspectionRowDto data) {
        this.title = data.getTitle();
        this.content = data.getSummary();
        this.start = data.getStart();
        this.end = data.getEnd();
    }
}
