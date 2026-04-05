package com.project.kiosk.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Rate Limit 설정 (테스트에서는 application-test.yml 로 비활성화 가능) */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.ratelimit")
public class RateLimitProperties {

    /** false 이면 RateLimitFilter 는 통과만 함 */
    private boolean enabled = true;

    /** IP 당 분당 허용 API 요청 수 */
    private int apiRequestsPerMinute = 60;

    /** 로그인 실패 허용 횟수(동일 IP) */
    private int loginMaxFailures = 5;

    /** 초과 시 차단 시간(초) */
    private int loginBlockSeconds = 600;

    /** 로그인 실패 카운트 윈도우(초) — 이 시간 내 실패만 누적 */
    private int loginFailureWindowSeconds = 900;
}
