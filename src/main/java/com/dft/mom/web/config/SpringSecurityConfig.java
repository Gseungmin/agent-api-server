package com.dft.mom.web.config;

import com.dft.mom.domain.service.LoginService;
import com.dft.mom.web.filter.authentication.CustomUserDetailsService;
import com.dft.mom.web.filter.authentication.JwtAppleAuthenticationFilter;
import com.dft.mom.web.filter.authentication.JwtKakaoAuthenticationFilter;
import com.dft.mom.web.filter.authorization.JwtAuthorizationRsaFilter;
import com.dft.mom.web.filter.exception.CustomAuthenticationEntryPoint;
import com.dft.mom.web.filter.exception.CustomAuthenticationFailureHandler;
import com.dft.mom.web.filter.security.BlacklistFilter;
import com.dft.mom.web.filter.security.LLMAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationFailureHandler authFailureHandler;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final LoginService loginService;
    private final BlacklistFilter blacklistFilter;
    private final LLMAuthFilter llmAuthFilter;
    private final JwtAuthorizationRsaFilter jwtAuthorizationRsaFilter;

    private String[] permitAllUrlPatterns() {
        return new String[] {
                "/",
                "/auth/kakao",
                "/auth/login/non",
                "/auth/login/admin",
                "/auth/apple",
                "/common/version-check"
        };
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests ->
                        requests
                                .requestMatchers(permitAllUrlPatterns()).permitAll()
                                .anyRequest().authenticated()
                )
                .exceptionHandling(handler ->
                        handler.authenticationEntryPoint(authenticationEntryPoint)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.userDetailsService(userDetailsService);

        JwtKakaoAuthenticationFilter jwtKakaoAuthenticationFilter =
                new JwtKakaoAuthenticationFilter(http, loginService);
        jwtKakaoAuthenticationFilter.setAuthenticationFailureHandler(authFailureHandler);
        jwtKakaoAuthenticationFilter.setFilterProcessesUrl("/auth/login/kakao");

        JwtAppleAuthenticationFilter jwtAppleAuthenticationFilter =
                new JwtAppleAuthenticationFilter(http, loginService);
        jwtAppleAuthenticationFilter.setAuthenticationFailureHandler(authFailureHandler);
        jwtAppleAuthenticationFilter.setFilterProcessesUrl("/auth/login/apple");

        http
                .addFilter(jwtKakaoAuthenticationFilter)
                .addFilter(jwtAppleAuthenticationFilter)
                .addFilterBefore(jwtAuthorizationRsaFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(llmAuthFilter, JwtAuthorizationRsaFilter.class)
                .addFilterBefore(blacklistFilter, CorsFilter.class);

        return http.build();
    }
}