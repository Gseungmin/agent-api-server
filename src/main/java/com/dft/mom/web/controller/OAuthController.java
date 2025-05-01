package com.dft.mom.web.controller;

import com.dft.mom.domain.service.CacheOAuthService;
import com.dft.mom.domain.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.dft.mom.domain.validator.MemberValidator.validateAuthentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class OAuthController {

    private final RoleService roleService;
    private final CacheOAuthService authService;

    public void validateOAuth(
            Authentication authentication,
            HttpServletRequest request
    ) {
        validateAuthentication(authentication, request);
        String id = authentication.getName();
        String role = roleService.getMemberRole(authentication);
        authService.increaseAndValidate(id, role);
    }
}