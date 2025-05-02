package com.dft.mom.web.filter.authorization;

import com.dft.mom.domain.service.LoginService;
import com.dft.mom.web.exception.ExceptionType;
import com.dft.mom.web.exception.member.MemberException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Key;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dft.mom.domain.function.FunctionUtil.getToken;
import static com.dft.mom.domain.util.CommonConstants.*;
import static com.dft.mom.domain.util.EntityConstants.ADMIN_STR;
import static com.dft.mom.domain.util.EntityConstants.MEMBER_STR;
import static com.dft.mom.web.exception.ExceptionType.*;

@Component
@RequiredArgsConstructor
public class JwtAuthorizationRsaFilter extends OncePerRequestFilter {

    private final Key key;
    private final LoginService loginService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        request.getMethod();
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return (pathMatcher.match("/", path)
                || pathMatcher.match("/auth/apple", path)
                || pathMatcher.match("/auth/kakao", path)
                || pathMatcher.match("/auth/login/non", path)
                || pathMatcher.match("/common/version-check", path));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException {
        try {
            String token = getToken(request);
            if (token == null) {
                setException(request, TOKEN_NOT_EXIST, chain, response);
                return;
            }

            Claims claims = loginService.validateToken(key, token);
            List<String> roles = (List<String>) claims.get("role");

            if (isValidateNeed(request)) {
                loginService.validateLogin(token, claims.getSubject(), ACCESS_TOKEN);
            }

            validateNonMember(request, roles);
            createAuthentication(request, claims, roles);
        } catch (ExpiredJwtException e) {
            setException(request, TOKEN_EXPIRED, chain, response);
            return;
        } catch (MemberException e) {
            setException(request, UN_AUTH_NON_MEMBER, chain, response);
            return;
        } catch (Exception e) {
            setException(request, TOKEN_INVALID, chain, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private void setException(
            HttpServletRequest request,
            ExceptionType errorType,
            FilterChain chain,
            HttpServletResponse response
    ) throws IOException, ServletException {
        request.setAttribute("exception", errorType);
        chain.doFilter(request, response);
    }

    private void createAuthentication(
            HttpServletRequest request,
            Claims claims,
            List<String> roles
    ) {
        String id = claims.getSubject();
        Set<GrantedAuthority> grantedAuthorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        if (id != null && !grantedAuthorities.isEmpty()) {
            UserDetails user = User.builder()
                    .username(id)
                    .password(UUID.randomUUID().toString())
                    .authorities(grantedAuthorities)
                    .build();

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            request.setAttribute("exception", TOKEN_INVALID);
        }
    }

    private boolean isValidateNeed(HttpServletRequest request) {
        if (request.getRequestURI().equals("/auth/reissue")) {
            return false;
        }

        if (!request.getMethod().equalsIgnoreCase("GET")) {
            return true;
        }

        return VALIDATE_GET_ROUTE.contains(request.getRequestURI());
    }

    private void validateNonMember(
            HttpServletRequest request,
            List<String> roles
    ) {
        if (NON_MEMBER_ROUTE.contains(request.getRequestURI())) {
            return;
        }

        if (roles.contains(MEMBER_STR) || roles.contains(ADMIN_STR)) {
            return;
        }

        throw new MemberException(
                UN_AUTH_NON_MEMBER.getCode(),
                UN_AUTH_NON_MEMBER.getErrorMessage()
        );
    }
}
