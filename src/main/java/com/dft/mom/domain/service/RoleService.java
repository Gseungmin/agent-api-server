package com.dft.mom.domain.service;

import com.dft.mom.web.exception.member.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

import static com.dft.mom.domain.util.EntityConstants.ADMIN_STR;
import static com.dft.mom.domain.util.EntityConstants.NON_MEMBER_STR;
import static com.dft.mom.web.exception.ExceptionType.*;

@Service
@RequiredArgsConstructor
public class RoleService {

    public void validateAdmin(String role) {
        if (!Objects.equals(role, ADMIN_STR)) {
            throw new MemberException(
                    ADMIN_ONLY.getCode(),
                    ADMIN_ONLY.getErrorMessage()
            );
        }
    }

    public void validateNon(String role) {
        if (Objects.equals(role, NON_MEMBER_STR)) {
            throw new MemberException(
                    UN_AUTH_NON_MEMBER.getCode(),
                    UN_AUTH_NON_MEMBER.getErrorMessage()
            );
        }
    }

    public String getMemberRole(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return authorities.stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new MemberException(
                        MEMBER_NOT_EXIST.getCode(),
                        MEMBER_NOT_EXIST.getErrorMessage()
                ));
    }
}