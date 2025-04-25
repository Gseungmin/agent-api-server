package com.dft.mom.service;

import com.dft.mom.domain.cache.TwoLevelCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.data.redis.RedisConnectionFailureException;

import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwoLevelCacheTest {

    @Mock Cache redis;
    @Mock Cache local;
    TwoLevelCache cache;

    @BeforeEach
    void setUp() {
        cache = new TwoLevelCache(redis, local);
    }

    @Test
    @DisplayName("1. REDIS 캐시 미스 시 DB 호출 후")
    void 캐시테스트1() throws Exception {
        when(redis.get(eq("k"), any(Callable.class)))
                .thenAnswer(inv -> {
                    Callable<String> loader = inv.getArgument(1);
                    return loader.call();
                });

        Callable<String> db = mock(Callable.class);
        when(db.call()).thenReturn("DB");

        assertEquals("DB", cache.get("k", db));

        verify(redis, times(1)).get(eq("k"), any(Callable.class));
        verify(local, never()).get(any(), any(Callable.class));
        verify(db, times(1)).call();
    }

    @Test
    @DisplayName("2. REDIS 캐시 히트 시 바로 반환")
    void 캐시테스트2() throws Exception {
        when(redis.get(eq("k"), any(Callable.class))).thenReturn("R");

        Callable<String> db = mock(Callable.class);

        assertEquals("R", cache.get("k", db));
        verify(redis).get(eq("k"), any(Callable.class));
        verify(local, never()).get(any(), any(Callable.class));
        verify(db, never()).call();
    }

    @Test
    @DisplayName("3. REDIS 장애 시 로컬 캐시 미스로 DB 조회 사용")
    void 캐시테스트3() throws Exception {
        when(redis.get(eq("k"), any(Callable.class)))
                .thenThrow(new RuntimeException(new RedisConnectionFailureException("레디스 장애 발생")));
        when(local.get(eq("k"), any(Callable.class)))
                .thenAnswer(inv -> ((Callable<?>) inv.getArgument(1)).call());

        Callable<String> db = mock(Callable.class);
        when(db.call()).thenReturn("DB");

        assertEquals("DB", cache.get("k", db));
        verify(redis, times(1)).get(eq("k"), any(Callable.class));
        verify(local, times(1)).get(eq("k"), any(Callable.class));
        verify(redis, never()).get(eq("m"), any(Callable.class));
        verify(local, never()).get(eq("m"), any(Callable.class));
        verify(db, times(1)).call();
    }

    @Test
    @DisplayName("4. REDIS 장애 시 로컬 캐시 사용")
    void 캐시테스트4() throws Exception {
        when(redis.get(eq("k"), any(Callable.class)))
                .thenThrow(new RuntimeException(new RedisConnectionFailureException("레디스 장애 발생")));
        when(local.get(eq("k"), any(Callable.class))).thenReturn("CACHED");
        Callable<String> db = mock(Callable.class);

        assertEquals("CACHED", cache.get("k", db));
        verify(redis, times(1)).get(eq("k"), any(Callable.class));
        verify(local, times(1)).get(eq("k"), any(Callable.class));
        verify(redis, never()).get(eq("m"), any(Callable.class));
        verify(local, never()).get(eq("m"), any(Callable.class));
        verify(db, never()).call();
    }

    @Test
    @DisplayName("5. REDIS PUT 성공 시 로컬 캐시 반영 안됨")
    void 캐시테스트5() {
        // when
        doNothing().when(redis).put("k", "v");

        //given
        cache.put("k", "v");

        // then
        verify(redis, times(1)).put("k", "v");
        verify(local, never()).put(any(), any());
    }

    @Test
    @DisplayName("6. REDIS 장애 시 로컬 캐시 반영 됨")
    void 캐시테스트6() {
        // when
        doThrow(new RuntimeException(new RedisConnectionFailureException("레디스 장애 발생")))
                .when(redis).put("k", "v");

        // given
        cache.put("k", "v");

        // then
        verify(redis, times(1)).put("k", "v");
        verify(local, times(1)).put("k", "v");
    }

    @Test
    @DisplayName("7. REDIS 장애 시 로컬 캐시 반영 됨")
    void 캐시테스트7() {
        // given when
        doThrow(new RuntimeException("알수없는 에러")).when(redis).put("k", "v");

        // then
        assertThrows(RuntimeException.class, () -> cache.put("k", "v"));
        verify(local, never()).put(any(), any());
    }
}
