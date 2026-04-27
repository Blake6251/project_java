package com.project.kiosk.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenBlacklistService {

    private static final String PREFIX = "blacklist:";
    private static final String VALUE = "logout";

    private final StringRedisTemplate redisTemplate;

    public void blacklist(String token, long ttlMillis) {
        try {
            redisTemplate.opsForValue().set(PREFIX + token, VALUE, Duration.ofMillis(ttlMillis));
        } catch (Exception e) {
            log.warn("Redis unavailable - blacklist write skipped");
        }
    }

    public boolean isBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + token));
        } catch (Exception e) {
            // Redis 장애 시 전체 인증을 막지 않기 위해 블랙리스트 미존재로 처리한다.
            log.warn("Redis unavailable - blacklist check skipped");
            return false;
        }
    }
}
