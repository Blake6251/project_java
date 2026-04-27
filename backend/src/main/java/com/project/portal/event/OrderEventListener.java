package com.project.kiosk.event;

import com.project.kiosk.domain.NotificationType;
import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.service.EmailService;
import com.project.kiosk.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Async
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        messagingTemplate.convertAndSend("/topic/orders", event.getResponse());
        emailService.sendOrderCreatedEmail(event.getOrder());
        notificationService.save(
                NotificationType.ORDER_CREATED,
                "주문이 생성되었습니다. orderId=" + event.getOrder().getId(),
                event.getOrder().getId()
        );
    }

    @Async
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        emailService.sendOrderStatusChangedEmail(event.getOrder());
        NotificationType type = event.getOrder().getStatus() == OrderStatus.CANCELLED
                ? NotificationType.ORDER_CANCELLED : NotificationType.STATUS_CHANGED;
        notificationService.save(
                type,
                "주문 상태가 변경되었습니다. status=" + event.getOrder().getStatus(),
                event.getOrder().getId()
        );
    }
}
