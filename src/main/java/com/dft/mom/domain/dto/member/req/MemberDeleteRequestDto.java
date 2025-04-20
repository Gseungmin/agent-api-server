package com.dft.mom.domain.dto.member.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberDeleteRequestDto {

    private List<Boolean> options;
    private String reason;
}
