package com.dft.mom.domain.service;

import com.dft.mom.domain.dto.member.res.TokenResponseDto;
import com.dft.mom.domain.entity.member.Member;
import com.dft.mom.domain.redis.LoginRedisService;
import com.dft.mom.web.exception.member.MemberException;
import com.dft.mom.web.filter.apple.ApplePublicKeyGenerator;
import com.dft.mom.web.filter.apple.ApplePublicKeyResponse;
import com.dft.mom.web.signature.JWTSigner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import static com.dft.mom.domain.util.CommonConstants.APPLE_API;
import static com.dft.mom.domain.util.CommonConstants.KAKAO_API;
import static com.dft.mom.domain.util.TimeOutConstants.ACCESS_TOKEN_EXPIRED;
import static com.dft.mom.domain.util.TimeOutConstants.REFRESH_TOKEN_EXPIRED;
import static com.dft.mom.web.exception.ExceptionType.*;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JWTSigner jwtSigner;
    private final LoginRedisService loginRedisService;
    private final RestTemplate restTemplate;
    private final ApplePublicKeyGenerator applePublicKeyGenerator;

    public void validateLogin(String token, String userId, String prefix) {
        if (token == null) {
            throw new MemberException(TOKEN_INVALID.getCode(), TOKEN_INVALID.getErrorMessage());
        }

        String tokenByPid = loginRedisService.getTokenById(prefix, userId);

        if (!Objects.equals(tokenByPid, token)) {
            throw new MemberException(MULTI_LOGIN.getCode(), MULTI_LOGIN.getErrorMessage());
        }
    }

    /*리프레시 토큰 발급*/
    public TokenResponseDto createToken(String memberId) {
        String accessToken = jwtSigner.getJwtToken(memberId, ACCESS_TOKEN_EXPIRED);
        String refreshToken = jwtSigner.getJwtToken(memberId, REFRESH_TOKEN_EXPIRED);
        loginRedisService.saveToken(memberId, accessToken, refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    /*회원가입 토큰 발급*/
    public TokenResponseDto createToken(Member member) {
        String accessToken = jwtSigner.getJwtToken(member, ACCESS_TOKEN_EXPIRED);
        String refreshToken = jwtSigner.getJwtToken(member, REFRESH_TOKEN_EXPIRED);
        loginRedisService.saveToken(member.getId().toString(), accessToken, refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    /*로그인 토큰 발급*/
    public TokenResponseDto createToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtSigner.getJwtToken(user, ACCESS_TOKEN_EXPIRED);
        String refreshToken = jwtSigner.getJwtToken(user, REFRESH_TOKEN_EXPIRED);
        loginRedisService.saveToken(user.getUsername(), accessToken, refreshToken);
        return new TokenResponseDto(accessToken, refreshToken);
    }

    /*토큰 검증*/
    public Claims validateToken(Key key, String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String accessToKakao(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(KAKAO_API, HttpMethod.POST, entity, String.class);

            JsonElement element = JsonParser.parseString(Objects.requireNonNull(response.getBody()));
            return element.getAsJsonObject().get("id").toString();
        } catch (RestClientException e) {
            throw new MemberException(SOCIAL_CONNECT_FAILED.getCode(), SOCIAL_CONNECT_FAILED.getErrorMessage());
        }
    }

    public void accessToApple(String idToken, String User) {
        try {
            Map<String, String> tokenHeaders = parseHeaders(idToken);
            ApplePublicKeyResponse appleKeys = restTemplate.getForObject(APPLE_API, ApplePublicKeyResponse.class);

            PublicKey publicKey = applePublicKeyGenerator.generatePublicKey(tokenHeaders, appleKeys);
            Claims claims = getTokenClaims(idToken, publicKey);

            if (Objects.equals(User, claims.getSubject())) {
                return;
            }

            throw new MemberException(SOCIAL_CONNECT_FAILED.getCode(), SOCIAL_CONNECT_FAILED.getErrorMessage());
        } catch (Exception e) {
            throw new MemberException(SOCIAL_CONNECT_FAILED.getCode(), SOCIAL_CONNECT_FAILED.getErrorMessage());
        }
    }

    public Map<String, String> parseHeaders(String token) throws JsonProcessingException {
        String header = token.split("\\.")[0];
        return new ObjectMapper().readValue(decodeHeader(header), Map.class);
    }


    public Claims getTokenClaims(String token, PublicKey publicKey) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String decodeHeader(String token) {
        return new String(Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
    }
}