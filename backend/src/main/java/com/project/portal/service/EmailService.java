package com.project.portal.service;

import com.project.portal.domain.Order;
import com.project.portal.domain.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOrderCreatedEmail(Order order) {
        send(
                order.getUser().getUsername() + "@example.com",
                "[Portal] 二쇰Ц ?앹꽦 ?뚮┝",
                "二쇰Ц?? " + order.getUser().getUsername() + "\n硫붾돱: " + order.getMenuName()
                        + "\n?섎웾: " + order.getQuantity() + "\n?곹깭: " + order.getStatus()
        );
    }

    @Async
    public void sendOrderStatusChangedEmail(Order order) {
        send(
                order.getUser().getUsername() + "@example.com",
                "[Portal] 二쇰Ц ?곹깭 蹂寃??뚮┝",
                "二쇰Ц?? " + order.getUser().getUsername() + "\n硫붾돱: " + order.getMenuName()
                        + "\n?섎웾: " + order.getQuantity() + "\n?꾩옱 ?곹깭: " + order.getStatus()
        );
    }

    @Async
    public void sendPaymentStatusEmail(Payment payment) {
        Order order = payment.getOrder();
        send(
                order.getUser().getUsername() + "@example.com",
                "[Portal] 寃곗젣 ?곹깭 ?뚮┝",
                "二쇰Ц?? " + order.getUser().getUsername() + "\n硫붾돱: " + order.getMenuName()
                        + "\n?섎웾: " + order.getQuantity() + "\n寃곗젣 ?곹깭: " + payment.getStatus()
        );
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            // 硫붿씪 ?ㅽ뙣??鍮꾩쫰?덉뒪 ?몃옖??뀡??源⑥? ?딅룄濡?濡쒓렇留??④릿??
            log.warn("Email send failed: {}", e.getMessage());
        }
    }
}
