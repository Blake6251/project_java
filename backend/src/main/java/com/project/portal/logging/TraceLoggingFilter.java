package com.project.portal.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceLoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String traceId = UUID.randomUUID().toString();
        long start = System.currentTimeMillis();
        MDC.put(TRACE_ID, traceId);

        try {
            log.info("REQ method={} uri={} headers={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    extractHeaders(request));

            filterChain.doFilter(request, response);
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("RES method={} uri={} status={} elapsedMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    elapsed);
            MDC.remove(TRACE_ID);
        }
    }

    private String extractHeaders(HttpServletRequest request) {
        List<String> names = Collections.list(request.getHeaderNames());
        return names.stream()
                .map(name -> name + "=" + normalizeHeaderValue(name, request.getHeader(name)))
                .collect(Collectors.joining(", "));
    }

    private String normalizeHeaderValue(String name, String value) {
        if (value == null) {
            return "";
        }
        String lower = name.toLowerCase();
        if ("authorization".equals(lower) || "cookie".equals(lower)) {
            return "***";
        }
        return value;
    }
}
