package com.project.portal.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND(404, "User not found"),
    ORDER_NOT_FOUND(404, "Order not found"),
    PAYMENT_NOT_FOUND(404, "Payment not found"),
    MENU_NOT_FOUND(404, "Menu not found"),
    NOTIFICATION_NOT_FOUND(404, "Notification not found"),
    UNAUTHORIZED(401, "Authentication required"),
    FORBIDDEN(403, "Access denied"),
    INVALID_INPUT(400, "Invalid input"),
    DUPLICATE_USERNAME(400, "Username already exists"),
    TOO_MANY_REQUESTS(429, "Too many requests"),
    LOGIN_ATTEMPTS_EXCEEDED(429, "Login attempts exceeded");

    private final int status;
    private final String message;
}
