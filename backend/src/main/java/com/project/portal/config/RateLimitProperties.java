package com.project.portal.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Rate Limit ?ㅼ젙 (?뚯뒪?몄뿉?쒕뒗 application-test.yml 濡?鍮꾪솢?깊솕 媛?? */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.ratelimit")
public class RateLimitProperties {

    /** false ?대㈃ RateLimitFilter ???듦낵留???*/
    private boolean enabled = true;

    /** IP ??遺꾨떦 ?덉슜 API ?붿껌 ??*/
    private int apiRequestsPerMinute = 60;

    /** 濡쒓렇???ㅽ뙣 ?덉슜 ?잛닔(?숈씪 IP) */
    private int loginMaxFailures = 5;

    /** 珥덇낵 ??李⑤떒 ?쒓컙(珥? */
    private int loginBlockSeconds = 600;

    /** 濡쒓렇???ㅽ뙣 移댁슫???덈룄??珥? ?????쒓컙 ???ㅽ뙣留??꾩쟻 */
    private int loginFailureWindowSeconds = 900;
}
