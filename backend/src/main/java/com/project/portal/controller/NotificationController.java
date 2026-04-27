package com.project.portal.controller;

import com.project.portal.dto.response.NotificationResponse;
import com.project.portal.dto.response.PageResponse;
import com.project.portal.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification history API")
@PreAuthorize("hasRole('ADMIN')")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "List notifications", description = "Admin paginated notification history")
    @GetMapping
    public ResponseEntity<PageResponse<NotificationResponse>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(notificationService.findAll(PageRequest.of(page, size)));
    }

    @Operation(summary = "Mark as read", description = "Mark notification as read")
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markRead(id));
    }

    @Operation(summary = "Unread notification count", description = "Get unread notification count")
    @GetMapping("/unread-count")
    public ResponseEntity<Long> unreadCount() {
        return ResponseEntity.ok(notificationService.unreadCount());
    }
}
