package com.dft.mom.domain.entity.member;

import com.dft.mom.domain.dto.member.req.MemberAppleCreateRequestDto;
import com.dft.mom.domain.dto.member.req.MemberCreateRequestDto;
import com.dft.mom.domain.entity.base.BaseEntity;
import com.dft.mom.domain.entity.family.Family;
import com.dft.mom.web.security.EncryptorConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.dft.mom.domain.util.EntityConstants.DEFAULT_ALARM_LIST;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(generator = "member_seq_id")
    @GenericGenerator(name = "member_seq_id", strategy = "com.dft.baby.domain.generator.MemberIDGenerator")
    @Column(name = "memberId")
    private Long id;

    private String socialId;
    private String socialType;

    private String name;
    private LocalDate birth;
    private Integer gender;
    private Integer relation;

    private String device;
    private Integer alarmList;
    private Integer agreeList;

    @Convert(converter = EncryptorConverter.class)
    private String profileImage;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> roles = new ArrayList<>();

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "authId")
    private Auth auth;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyId")
    private Family family;

    public Member(MemberCreateRequestDto dto, String socialId) {
        this.socialId = socialId;
        this.socialType = "KAKAO";
        this.name = dto.getName().trim();
        this.agreeList = dto.getAgree();
        this.relation = dto.getRelation();
        this.alarmList = DEFAULT_ALARM_LIST;
        this.getRoles().add("USER");
    }

    public Member(MemberAppleCreateRequestDto dto, String socialId) {
        this.socialId = socialId;
        this.socialType = "APPLE";
        this.name = dto.getName().trim();
        this.agreeList = dto.getAgree();
        this.relation = dto.getRelation();
        this.alarmList = DEFAULT_ALARM_LIST;
        this.getRoles().add("USER");
    }

    public void deleteMember() {
        this.socialId = null;
        this.socialType = null;
        this.name = null;
        this.profileImage = null;
        this.device = null;
    }
}
