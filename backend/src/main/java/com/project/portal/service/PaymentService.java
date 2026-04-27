package com.project.portal.service;

import com.project.portal.domain.Order;
import com.project.portal.domain.OrderStatus;
import com.project.portal.domain.Payment;
import com.project.portal.domain.PaymentStatus;
import com.project.portal.dto.request.PaymentCreateRequest;
import com.project.portal.dto.response.PaymentResponse;
import com.project.portal.exception.CustomException;
import com.project.portal.exception.ErrorCode;
import com.project.portal.repository.OrderRepository;
import com.project.portal.repository.PaymentRepository;
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

        // 寃곗젣 ?꾨즺 ??二쇰Ц ?곹깭瑜??먮룞?쇰줈 以鍮꾩쨷?쇰줈 ?꾪솚?쒕떎.
        if (status == PaymentStatus.PAID) {
            payment.getOrder().setStatus(OrderStatus.IN_PROGRESS);
        }
        emailService.sendPaymentStatusEmail(payment);

        return toResponse(payment);
    }

    private void validateOwner(Order order, String username, boolean isAdmin) {
        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("?대떦 寃곗젣???묎렐??沅뚰븳???놁뒿?덈떎.");
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
