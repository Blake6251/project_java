package com.project.portal.event;

import com.project.portal.domain.NotificationType;
import com.project.portal.domain.OrderStatus;
import com.project.portal.service.EmailService;
import com.project.portal.service.NotificationService;
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
                "二쇰Ц???앹꽦?섏뿀?듬땲?? orderId=" + event.getOrder().getId(),
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
                "二쇰Ц ?곹깭媛 蹂寃쎈릺?덉뒿?덈떎. status=" + event.getOrder().getStatus(),
                event.getOrder().getId()
        );
    }
}
