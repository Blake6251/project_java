package com.project.portal.service;

import com.project.portal.config.RateLimitProperties;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 濡쒓렇???ㅽ뙣 ?잛닔 諛?IP 李⑤떒 (Redis).
 * Redis ?μ븷 ??fail-open (李⑤떒?섏? ?딆쓬).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginRateLimitService {

    private static final String BLOCK_PREFIX = "ratelimit:login:block:";
    private static final String COUNT_PREFIX = "ratelimit:login:count:";

    private final StringRedisTemplate stringRedisTemplate;
    private final RateLimitProperties rateLimitProperties;

    /** ?꾩옱 IP媛 濡쒓렇??李⑤떒 ?곹깭?몄? */
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

    /** 濡쒓렇???ㅽ뙣 1??湲곕줉. ?꾧퀎 ?꾨떖 ??BLOCK ???ㅼ젙 */
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

    /** 濡쒓렇???깃났 ??移댁슫?맞룹감???댁젣 */
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
