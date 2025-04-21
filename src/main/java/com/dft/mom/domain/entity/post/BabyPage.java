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

import static com.dft.mom.domain.util.PostConstants.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(indexes = {
        @Index(name = "idx_baby_page_type_period", columnList = "type, period"),
        @Index(name = "idx_baby_page_name", columnList = "excelName")
})
public class BabyPage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "babyPageId")
    private Long id;

    @Column(nullable = false)
    private Integer type;

    @Column(nullable = false)
    private Integer period;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "babyPage")
    private List<BabyPageItem> babyPageItemList = new ArrayList<>();

    public BabyPage(Integer type, Integer period) {
        this.type = type;
        this.period = period;
    }
}
