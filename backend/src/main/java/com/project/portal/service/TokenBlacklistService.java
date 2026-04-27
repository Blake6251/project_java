package com.project.portal.service;

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
            // Redis ?μ븷 ???꾩껜 ?몄쬆??留됱? ?딄린 ?꾪빐 釉붾옓由ъ뒪??誘몄〈?щ줈 泥섎━?쒕떎.
            log.warn("Redis unavailable - blacklist check skipped");
            return false;
        }
    }
}
