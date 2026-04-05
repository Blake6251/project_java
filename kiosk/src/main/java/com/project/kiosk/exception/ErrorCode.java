package com.project.kiosk.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(404, "유저를 찾을 수 없습니다"),
    ORDER_NOT_FOUND(404, "주문을 찾을 수 없습니다"),
    PAYMENT_NOT_FOUND(404, "결제를 찾을 수 없습니다"),
    MENU_NOT_FOUND(404, "메뉴를 찾을 수 없습니다"),
    NOTIFICATION_NOT_FOUND(404, "알림을 찾을 수 없습니다"),
    UNAUTHORIZED(401, "인증이 필요합니다"),
    FORBIDDEN(403, "권한이 없습니다"),
    INVALID_INPUT(400, "잘못된 입력입니다"),
    DUPLICATE_USERNAME(400, "이미 존재하는 사용자명입니다"),
    TOO_MANY_REQUESTS(429, "요청이 너무 많습니다"),
    LOGIN_ATTEMPTS_EXCEEDED(429, "로그인 시도 횟수를 초과했습니다");

    private final int status;
    private final String message;
}
