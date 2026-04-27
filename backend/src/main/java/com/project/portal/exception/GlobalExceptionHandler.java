package com.project.kiosk.exception;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TRACE_ID_KEY = "traceId";

    private String traceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();
        log.warn("traceId={} customException status={} message={}", traceId(), code.getStatus(), e.getMessage());
        ErrorResponse body = ErrorResponse.of(code.getStatus(), e.getMessage());
        HttpHeaders headers = new HttpHeaders();
        if (code == ErrorCode.LOGIN_ATTEMPTS_EXCEEDED) {
            headers.add(HttpHeaders.RETRY_AFTER, "600");
        }
        if (code == ErrorCode.TOO_MANY_REQUESTS) {
            headers.add(HttpHeaders.RETRY_AFTER, "60");
        }
        return ResponseEntity.status(code.getStatus()).headers(headers).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException e) {
        log.warn("traceId={} accessDenied message={}", traceId(), e.getMessage());
        ErrorCode code = ErrorCode.FORBIDDEN;
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(code.getStatus(), code.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentication(AuthenticationException e) {
        ErrorCode code = ErrorCode.UNAUTHORIZED;
        log.warn("traceId={} authFailed message={}", traceId(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(code.getStatus(), code.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        if (msg.isBlank()) {
            msg = ErrorCode.INVALID_INPUT.getMessage();
        }
        ErrorCode code = ErrorCode.INVALID_INPUT;
        log.warn("traceId={} validationError message={}", traceId(), msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(code.getStatus(), msg));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("traceId={} unhandledException message={}", traceId(), e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "서버 오류가 발생했습니다"));
    }
}
