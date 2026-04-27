package com.project.kiosk.service;

import com.project.kiosk.domain.Order;
import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.domain.Payment;
import com.project.kiosk.domain.PaymentStatus;
import com.project.kiosk.dto.request.PaymentCreateRequest;
import com.project.kiosk.dto.response.PaymentResponse;
import com.project.kiosk.exception.CustomException;
import com.project.kiosk.exception.ErrorCode;
import com.project.kiosk.repository.OrderRepository;
import com.project.kiosk.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    @Transactional
    public PaymentResponse create(PaymentCreateRequest request, String username, boolean isAdmin) {
        Order order = orderRepository.findByIdWithUser(request.getOrderId())
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        validateOwner(order, username, isAdmin);

        Payment payment = Payment.builder()
                .order(order)
                .amount(request.getAmount())
                .status(PaymentStatus.CREATED)
                .build();

        Payment saved = paymentRepository.save(payment);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PaymentResponse findById(Long id, String username, boolean isAdmin) {
        Payment payment = paymentRepository.findByIdWithOrderAndUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
        validateOwner(payment.getOrder(), username, isAdmin);
        return toResponse(payment);
    }

    @Transactional
    @CacheEvict(value = "orders-admin-all", allEntries = true)
    public PaymentResponse updateStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findByIdWithOrderAndUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.setStatus(status);

        // 결제 완료 시 주문 상태를 자동으로 준비중으로 전환한다.
        if (status == PaymentStatus.PAID) {
            payment.getOrder().setStatus(OrderStatus.IN_PROGRESS);
        }
        emailService.sendPaymentStatusEmail(payment);

        return toResponse(payment);
    }

    private void validateOwner(Order order, String username, boolean isAdmin) {
        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("해당 결제에 접근할 권한이 없습니다.");
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .username(payment.getOrder().getUser().getUsername())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .orderStatus(payment.getOrder().getStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
