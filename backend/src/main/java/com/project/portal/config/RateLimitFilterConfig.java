package com.project.portal.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.portal.service.LoginRateLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * RateLimitFilter 瑜??쒕툝由??꾪꽣濡??깅줉.
 * TraceLoggingFilter(HIGHEST_PRECEDENCE) 吏곹썑???ㅽ뻾?섎룄濡??쒖꽌 吏??
 */
@Configuration
@RequiredArgsConstructor
public class RateLimitFilterConfig {

    private final RateLimitProperties rateLimitProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final LoginRateLimitService loginRateLimitService;

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration() {
        RateLimitFilter filter = new RateLimitFilter(
                rateLimitProperties,
                stringRedisTemplate,
                objectMapper,
                loginRateLimitService
        );
        FilterRegistrationBean<RateLimitFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE + 5);
        reg.addUrlPatterns("/*");
        return reg;
    }
}
