package com.dft.mom.domain.entity.family;

import com.dft.mom.domain.dto.baby.req.ParentingCreateRequestDto;
import com.dft.mom.domain.dto.baby.req.PregnancyCreateRequestDto;
import com.dft.mom.domain.entity.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;

import static com.dft.mom.domain.util.EntityConstants.BABY;
import static com.dft.mom.domain.util.EntityConstants.PREGNANT;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Baby extends BaseEntity {

    @Id
    @GeneratedValue(generator = "baby_seq_id")
    @GenericGenerator(name = "baby_seq_id", strategy = "com.dft.baby.domain.generator.BabyIDGenerator")
    @Column(name = "babyId")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyId")
    private Family family;

    private String name;
    private LocalDate birth;
    private Integer birthTime;
    private Integer type;
    private Integer gender;
    private LocalDate lastMoonDate;

    public Baby(ParentingCreateRequestDto dto) {
        this.name = dto.getName();
        this.birth = dto.getBirth();
        this.gender = dto.getGender();
        this.type = BABY;
    }

    public Baby(PregnancyCreateRequestDto dto) {
        this.name = dto.getName();
        this.birth = dto.getExpectedBirth();
        this.lastMoonDate = dto.getLastMenstrual();
        this.type = PREGNANT;
    }

    public void addFamily(Family family) {
        family.getBabyList().add(this);
        this.setFamily(family);
    }
}
