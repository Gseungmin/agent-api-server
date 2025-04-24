package com.dft.mom.domain.entity.member;

import com.dft.mom.domain.entity.base.BaseEntity;
import com.dft.mom.web.security.EncryptorConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "auth", indexes = {
        @Index(name = "idx_phone_number", columnList = "phoneNumber"),
        @Index(name = "idx_delete_date", columnList = "deleteDate"),
})
public class Auth extends BaseEntity {

    @Id
    @GeneratedValue(generator = "auth_seq_id")
    @GenericGenerator(name = "auth_seq_id", strategy = "com.dft.mom.domain.generator.AuthIDGenerator")
    @Column(name = "authId")
    private Long id;

    private boolean isDelete = false;

    @Column(name = "deleteDate")
    private LocalDate deleteDate;

    @Column(name = "phoneNumber", unique = true)
    @Convert(converter = EncryptorConverter.class)
    private String phoneNumber;
    private LocalDateTime lastChanged;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "auth")
    private Member member;

    public Auth(Member member) {
        this.phoneNumber = UUID.randomUUID().toString();
        this.member = member;
        member.setAuth(this);
    }
}
