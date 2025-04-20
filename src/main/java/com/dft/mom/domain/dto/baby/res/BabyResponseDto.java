package com.dft.mom.domain.dto.baby.res;

import com.dft.mom.domain.entity.family.Baby;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BabyResponseDto {

    private String id;
    private String name;
    private LocalDate birth;
    private Integer birthTime;
    private Integer gender;
    private Integer type;
    private LocalDate lastMoonDate;

    public BabyResponseDto(Baby baby) {
        this.id = baby.getId().toString();
        this.name = baby.getName();
        this.birth = baby.getBirth();
        this.birthTime = baby.getBirthTime();
        this.gender = baby.getGender();
        this.type = baby.getType();
        this.lastMoonDate = baby.getLastMoonDate();
    }
}
