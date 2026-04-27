package com.project.kiosk.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.kiosk.exception.ErrorCode;
import com.project.kiosk.exception.ErrorResponse;
import com.project.kiosk.service.LoginRateLimitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * API 분당 요청 제한 + 로그인 엔드포인트 사전 차단.
 * TraceLoggingFilter(HIGHEST) 다음에 동작하도록 Order 지정.
 */
@Slf4j
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String API_KEY_PREFIX = "ratelimit:api:";

    private final RateLimitProperties rateLimitProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final LoginRateLimitService loginRateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!rateLimitProperties.isEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();
        if (path.startsWith("/actuator/health")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        String ip = ClientIpResolver.resolve(request);

        if ("POST".equalsIgnoreCase(request.getMethod()) && "/api/auth/login".equals(path)) {
            if (loginRateLimitService.isBlocked(ip)) {
                writeTooManyRequests(response, ErrorCode.LOGIN_ATTEMPTS_EXCEEDED, rateLimitProperties.getLoginBlockSeconds());
                return;
            }
        }

        if (!tryConsumeApiQuota(ip, response)) {
            return;
        }

        filterChain.doFilter(request, response);
    }

    /** 분당 카운터 증가. 초과 시 false */
    private boolean tryConsumeApiQuota(String ip, HttpServletResponse response) throws IOException {
        try {
            String key = API_KEY_PREFIX + ip;
            Long c = stringRedisTemplate.opsForValue().increment(key);
            if (c != null && c == 1) {
                stringRedisTemplate.expire(key, Duration.ofMinutes(1));
            }
            if (c != null && c > rateLimitProperties.getApiRequestsPerMinute()) {
                writeTooManyRequests(response, ErrorCode.TOO_MANY_REQUESTS, 60);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.warn("Redis API rate limit failed, fail-open: {}", e.getMessage());
            return true;
        }
    }

    private void writeTooManyRequests(HttpServletResponse response, ErrorCode code, int retryAfterSeconds)
            throws IOException {
        response.setStatus(code.getStatus());
        response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
        ErrorResponse body = ErrorResponse.of(code.getStatus(), code.getMessage());
        objectMapper.writeValue(response.getWriter(), body);
    }
}
