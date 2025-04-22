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

import java.io.IOException;

import static com.dft.mom.domain.function.FunctionUtil.getClientIp;
import static com.dft.mom.web.exception.ExceptionType.BLOCKED_IP;

@Component
@Order(1)
@RequiredArgsConstructor
public class BlacklistFilter implements Filter {

    private final BlacklistService blacklistService;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String clientIp = getClientIp(httpRequest);
        String requestUri = httpRequest.getRequestURI();

        try {
            if (blacklistService.isBlacklisted(clientIp)) {
                httpRequest.setAttribute("exception", BLOCKED_IP);
                throw new CommonException(BLOCKED_IP.getCode(), BLOCKED_IP.getErrorMessage());
            }

            if (!isValidUrl(requestUri)) {
                blacklistService.addToBlacklist(clientIp);
                httpRequest.setAttribute("exception", BLOCKED_IP);
                throw new CommonException(BLOCKED_IP.getCode(), BLOCKED_IP.getErrorMessage());
            }

            chain.doFilter(request, response); }
        catch (Exception e) {
            authenticationEntryPoint.commence(httpRequest, httpResponse, new AuthenticationException(e.getMessage()) {});
        }
    }

    private boolean isValidUrl(String requestUri) {
        if (requestUri.startsWith("/auth")
                || requestUri.startsWith("/my")
                || requestUri.startsWith("/baby")
                || requestUri.startsWith("/page")
                || requestUri.startsWith("/family")
                || requestUri.startsWith("/error")) {
            return true;
        }

        return false;
    }
}
