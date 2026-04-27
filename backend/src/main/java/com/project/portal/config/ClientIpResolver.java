package com.project.kiosk.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;

/** X-Forwarded-For 등을 고려한 클라이언트 IP 추출 */
public final class ClientIpResolver {

    private ClientIpResolver() {
    }

    public static String resolve(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        String real = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(real)) {
            return real.trim();
        }
        return request.getRemoteAddr();
    }
}
