package com.project.kiosk.service;

import com.project.kiosk.domain.Order;
import com.project.kiosk.domain.Payment;
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
                "[Kiosk] 주문 생성 알림",
                "주문자: " + order.getUser().getUsername() + "\n메뉴: " + order.getMenuName()
                        + "\n수량: " + order.getQuantity() + "\n상태: " + order.getStatus()
        );
    }

    @Async
    public void sendOrderStatusChangedEmail(Order order) {
        send(
                order.getUser().getUsername() + "@example.com",
                "[Kiosk] 주문 상태 변경 알림",
                "주문자: " + order.getUser().getUsername() + "\n메뉴: " + order.getMenuName()
                        + "\n수량: " + order.getQuantity() + "\n현재 상태: " + order.getStatus()
        );
    }

    @Async
    public void sendPaymentStatusEmail(Payment payment) {
        Order order = payment.getOrder();
        send(
                order.getUser().getUsername() + "@example.com",
                "[Kiosk] 결제 상태 알림",
                "주문자: " + order.getUser().getUsername() + "\n메뉴: " + order.getMenuName()
                        + "\n수량: " + order.getQuantity() + "\n결제 상태: " + payment.getStatus()
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
            // 메일 실패는 비즈니스 트랜잭션을 깨지 않도록 로그만 남긴다.
            log.warn("Email send failed: {}", e.getMessage());
        }
    }
}
