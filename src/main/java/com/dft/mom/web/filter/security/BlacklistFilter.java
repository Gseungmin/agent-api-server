package com.dft.mom.web.filter.security;

import com.dft.mom.domain.service.BlacklistService;
import com.dft.mom.web.exception.CommonException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.dft.mom.domain.function.FunctionUtil.getClientIp;
import static com.dft.mom.web.exception.ExceptionType.BLOCKED_IP;

@Component
@RequiredArgsConstructor
public class BlacklistFilter extends OncePerRequestFilter {

    private final BlacklistService blacklistService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String clientIp  = getClientIp(request);
        String requestUri = request.getRequestURI();

        try {
            if (blacklistService.isBlacklisted(clientIp)) {
                request.setAttribute("exception", BLOCKED_IP);
                throw new CommonException(BLOCKED_IP.getCode(), BLOCKED_IP.getErrorMessage());
            }

            if (!isValidUrl(requestUri)) {
                blacklistService.addToBlacklist(clientIp);
                request.setAttribute("exception", BLOCKED_IP);
                throw new CommonException(BLOCKED_IP.getCode(), BLOCKED_IP.getErrorMessage());
            }

            chain.doFilter(request, response); }
        catch (Exception e) {
            authenticationEntryPoint.commence(request, response, new AuthenticationException(e.getMessage()) {});
        }
    }

    private boolean isValidUrl(String requestUri) {
        if (requestUri.startsWith("/auth")
                || requestUri.startsWith("/my")
                || requestUri.startsWith("/baby")
                || requestUri.startsWith("/page")
                || requestUri.startsWith("/item")
                || requestUri.startsWith("/oauth")
                || requestUri.startsWith("/family")
                || requestUri.startsWith("/error")) {
            return true;
        }

        return false;
    }
}
