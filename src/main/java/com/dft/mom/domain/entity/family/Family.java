package com.dft.mom.domain.entity.family;

import com.dft.mom.domain.entity.base.BaseEntity;
import com.dft.mom.domain.entity.member.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Family extends BaseEntity {

    @Id
    @GeneratedValue(generator = "family_seq_id")
    @GenericGenerator(name = "family_seq_id", strategy = "com.dft.mom.domain.generator.FamilyIDGenerator")
    @Column(name = "familyId")
    private Long id;
    private String code;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "family")
    private List<Member> memberList = new ArrayList<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "family")
    private List<Baby> babyList = new ArrayList<>();

    public Family(String code) {
        this.code = code;
    }

    public void addMember(Member member) {
        this.memberList.add(member);
        member.setFamily(this);
    }
}
