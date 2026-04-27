package com.project.portal.service;

import com.project.portal.domain.Notification;
import com.project.portal.domain.NotificationType;
import com.project.portal.dto.response.NotificationResponse;
import com.project.portal.dto.response.PageResponse;
import com.project.portal.exception.CustomException;
import com.project.portal.exception.ErrorCode;
import com.project.portal.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void save(NotificationType type, String message, Long orderId) {
        Notification notification = Notification.builder()
                .type(type)
                .message(message)
                .orderId(orderId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public PageResponse<NotificationResponse> findAll(Pageable pageable) {
        Page<Notification> page = notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
        return PageResponse.<NotificationResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .build();
    }

    @Transactional
    public NotificationResponse markRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        notification.setRead(true);
        return toResponse(notification);
    }

    @Transactional(readOnly = true)
    public long unreadCount() {
        return notificationRepository.countByIsReadFalse();
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .orderId(notification.getOrderId())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
