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

import static com.dft.mom.domain.util.PostConstants.DEFAULT_IMPORTANT;

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
    private Integer priority = DEFAULT_IMPORTANT;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inspection")
    private List<BabyPageItem> babyPageItemList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "inspection")
    private List<SubItem> subItemList = new ArrayList<>();

    public Inspection(Long itemId, String title, String content) {
        this.itemId = itemId;
        this.title = title;
        this.content = content;
    }

    public void updateInspection(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
