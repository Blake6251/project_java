package com.project.kiosk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.project.kiosk.domain.Order;
import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.domain.Payment;
import com.project.kiosk.domain.PaymentStatus;
import com.project.kiosk.domain.User;
import com.project.kiosk.dto.request.PaymentCreateRequest;
import com.project.kiosk.dto.response.PaymentResponse;
import com.project.kiosk.exception.CustomException;
import com.project.kiosk.exception.ErrorCode;
import com.project.kiosk.repository.OrderRepository;
import com.project.kiosk.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("결제 생성 성공")
    void create_success() {
        User user = User.builder().id(1L).username("user1").password("x").role("USER").createdAt(LocalDateTime.now()).build();
        Order order = Order.builder().id(10L).menuName("라떼").quantity(1).status(OrderStatus.CREATED).user(user).createdAt(LocalDateTime.now()).build();
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(10L);
        request.setAmount(4500);

        Payment saved = Payment.builder().id(100L).order(order).amount(4500).status(PaymentStatus.CREATED).createdAt(LocalDateTime.now()).build();

        when(orderRepository.findByIdWithUser(10L)).thenReturn(Optional.of(order));
        when(paymentRepository.save(org.mockito.ArgumentMatchers.any(Payment.class))).thenReturn(saved);

        PaymentResponse response = paymentService.create(request, "user1", false);
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.CREATED);
    }

    @Test
    @DisplayName("결제 생성 실패: 타인 주문")
    void create_forbidden() {
        User owner = User.builder().id(1L).username("owner").password("x").role("USER").createdAt(LocalDateTime.now()).build();
        Order order = Order.builder().id(10L).menuName("라떼").quantity(1).status(OrderStatus.CREATED).user(owner).createdAt(LocalDateTime.now()).build();
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setOrderId(10L);
        request.setAmount(4500);

        when(orderRepository.findByIdWithUser(10L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.create(request, "intruder", false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("결제 상태 PAID 변경 시 주문 상태 IN_PROGRESS 전환")
    void updateStatus_paid_updatesOrder() {
        User user = User.builder().id(1L).username("user1").password("x").role("USER").createdAt(LocalDateTime.now()).build();
        Order order = Order.builder().id(10L).menuName("라떼").quantity(1).status(OrderStatus.CREATED).user(user).createdAt(LocalDateTime.now()).build();
        Payment payment = Payment.builder().id(100L).order(order).amount(4500).status(PaymentStatus.CREATED).createdAt(LocalDateTime.now()).build();

        when(paymentRepository.findByIdWithOrderAndUser(100L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.updateStatus(100L, PaymentStatus.PAID);

        assertThat(response.getStatus()).isEqualTo(PaymentStatus.PAID);
        assertThat(response.getOrderStatus()).isEqualTo(OrderStatus.IN_PROGRESS);
    }

    @Test
    @DisplayName("결제 조회 실패: 결제 없음")
    void findById_notFound() {
        when(paymentRepository.findByIdWithOrderAndUser(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.findById(999L, "user1", false))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.PAYMENT_NOT_FOUND);
    }
}
