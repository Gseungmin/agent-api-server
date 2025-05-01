package com.dft.mom.web.filter.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LLMAuthFilter extends OncePerRequestFilter {

    @Value("${oauth.header-name}")
    private String headerName;

    @Value("${oauth.new-secret}")
    private String newSecret;

    @Value("${oauth.old-secret}")
    private String oldSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!request.getServletPath().startsWith("/oauth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String secret = request.getHeader(headerName);
        if (secret == null ||
                !(secret.equals(newSecret) || secret.equals(oldSecret))) {
            return;
        }

        filterChain.doFilter(request, response);
    }
}
