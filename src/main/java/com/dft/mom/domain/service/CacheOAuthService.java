package com.dft.mom.domain.service;

import com.dft.mom.web.exception.member.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

import static com.dft.mom.domain.util.EntityConstants.MEMBER_STR;
import static com.dft.mom.domain.util.EntityConstants.NON_MEMBER_STR;
import static com.dft.mom.web.exception.ExceptionType.MORE_FOR_MEMBER;
import static com.dft.mom.web.exception.ExceptionType.QUOTA_EXCEED;

@Service
@RequiredArgsConstructor
public class CacheOAuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "cache:llm:";
    private static final Duration TTL = Duration.ofHours(24);
    private static final int NONE_MEMBER_MAX_COUNT = 3;
    private static final int MEMBER_MAX_COUNT = 15;

    /* 호출 횟수를 1 증가시키고, 비회원이 5회를 넘으면 예외를 던지는 메서드 */
    public void increaseAndValidate(String memberId, String role) {
        String key = PREFIX + memberId;
        long count = redisTemplate.opsForValue().increment(key);

        if (count == 1L) {
            redisTemplate.expire(key, TTL);
        }

        if (NON_MEMBER_STR.equals(role) && count > NONE_MEMBER_MAX_COUNT) {
            throw new MemberException(
                    MORE_FOR_MEMBER.getCode(),
                    MORE_FOR_MEMBER.getErrorMessage()
            );
        }

        if (MEMBER_STR.equals(role) && count > MEMBER_MAX_COUNT) {
            throw new MemberException(
                    QUOTA_EXCEED.getCode(),
                    QUOTA_EXCEED.getErrorMessage()
            );
        }
    }
}