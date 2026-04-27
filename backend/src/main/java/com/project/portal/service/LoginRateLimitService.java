package com.project.kiosk.service;

import com.project.kiosk.config.RateLimitProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 로그인 실패 횟수 및 IP 차단 (Redis).
 * Redis 장애 시 fail-open (차단하지 않음).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginRateLimitService {

    private static final String BLOCK_PREFIX = "ratelimit:login:block:";
    private static final String COUNT_PREFIX = "ratelimit:login:count:";

    private final StringRedisTemplate stringRedisTemplate;
    private final RateLimitProperties rateLimitProperties;

    /** 현재 IP가 로그인 차단 상태인지 */
    public boolean isBlocked(String clientIp) {
        if (!rateLimitProperties.isEnabled()) {
            return false;
        }
        try {
            Boolean exists = stringRedisTemplate.hasKey(BLOCK_PREFIX + clientIp);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.warn("Redis login block check failed, fail-open: {}", e.getMessage());
            return false;
        }
    }

    /** 로그인 실패 1회 기록. 임계 도달 시 BLOCK 키 설정 */
    public void recordFailedAttempt(String clientIp) {
        if (!rateLimitProperties.isEnabled()) {
            return;
        }
        try {
            String countKey = COUNT_PREFIX + clientIp;
            Long c = stringRedisTemplate.opsForValue().increment(countKey);
            if (c != null && c == 1) {
                stringRedisTemplate.expire(countKey, Duration.ofSeconds(rateLimitProperties.getLoginFailureWindowSeconds()));
            }
            if (c != null && c >= rateLimitProperties.getLoginMaxFailures()) {
                stringRedisTemplate.opsForValue()
                        .set(BLOCK_PREFIX + clientIp, "1", Duration.ofSeconds(rateLimitProperties.getLoginBlockSeconds()));
                stringRedisTemplate.delete(countKey);
            }
        } catch (Exception e) {
            log.warn("Redis login failure record failed: {}", e.getMessage());
        }
    }

    /** 로그인 성공 시 카운트·차단 해제 */
    public void clearFailures(String clientIp) {
        if (!rateLimitProperties.isEnabled()) {
            return;
        }
        try {
            stringRedisTemplate.delete(COUNT_PREFIX + clientIp);
            stringRedisTemplate.delete(BLOCK_PREFIX + clientIp);
        } catch (Exception e) {
            log.warn("Redis login clear failed: {}", e.getMessage());
        }
    }
}
