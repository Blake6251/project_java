package com.project.kiosk.dto.response;

import com.project.kiosk.domain.NotificationType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private NotificationType type;
    private String message;
    private Long orderId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
