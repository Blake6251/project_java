package com.project.kiosk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.project.kiosk.domain.Order;
import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.domain.User;
import com.project.kiosk.dto.request.OrderRequest;
import com.project.kiosk.dto.response.OrderResponse;
import com.project.kiosk.exception.CustomException;
import com.project.kiosk.exception.ErrorCode;
import com.project.kiosk.repository.OrderRepository;
import com.project.kiosk.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;

/** OrderService 단위 테스트 (Mockito). 주문 생성·조회·상태 변경 및 권한 예외. */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderService orderService;

    @Test
    @DisplayName("주문 생성 성공: 저장 후 WebSocket 브로드캐스트")
    void create_success() {
        OrderRequest request = new OrderRequest();
        request.setMenuName("아메리카노");
        request.setQuantity(2);

        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("x")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        Order saved = Order.builder()
                .id(10L)
                .menuName("아메리카노")
                .quantity(2)
                .status(OrderStatus.CREATED)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        OrderResponse result = orderService.create(request, "user1");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getMenuName()).isEqualTo("아메리카노");
    }

    @Test
    @DisplayName("주문 생성 실패: 사용자 없음")
    void create_userNotFound_throws() {
        OrderRequest request = new OrderRequest();
        request.setMenuName("라떼");
        request.setQuantity(1);

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(request, "ghost"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("단건 조회 성공: 본인 주문")
    void findById_ownOrder_success() {
        User user = User.builder()
                .id(1L)
                .username("user1")
                .password("x")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(5L)
                .menuName("콜드브루")
                .quantity(1)
                .status(OrderStatus.CREATED)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findByIdWithUser(5L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.findById(5L, "user1", false);

        assertThat(response.getUsername()).isEqualTo("user1");
    }

    @Test
    @DisplayName("단건 조회 성공: 관리자는 타인 주문 조회 가능")
    void findById_admin_success() {
        User user = User.builder()
                .id(2L)
                .username("other")
                .password("x")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(7L)
                .menuName("에스프레소")
                .quantity(1)
                .status(OrderStatus.IN_PROGRESS)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findByIdWithUser(7L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.findById(7L, "admin1", true);

        assertThat(response.getUsername()).isEqualTo("other");
    }

    @Test
    @DisplayName("단건 조회 실패: 타인 주문 (일반 사용자)")
    void findById_forbidden_otherUser() {
        User user = User.builder()
                .id(2L)
                .username("owner")
                .password("x")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(8L)
                .menuName("티")
                .quantity(1)
                .status(OrderStatus.CREATED)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findByIdWithUser(8L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.findById(8L, "intruder", false))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("단건 조회 실패: 주문 없음")
    void findById_orderNotFound_throws() {
        when(orderRepository.findByIdWithUser(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(99L, "user1", false))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("관리자 전체 목록")
    void findAllForAdmin_returnsList() {
        User user = User.builder()
                .id(1L)
                .username("u1")
                .password("x")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        Order o1 = Order.builder()
                .id(1L)
                .menuName("m1")
                .quantity(1)
                .status(OrderStatus.CREATED)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findAllWithUser()).thenReturn(List.of(o1));

        List<OrderResponse> list = orderService.findAllForAdmin();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getMenuName()).isEqualTo("m1");
    }

    @Test
    @DisplayName("상태 변경 성공")
    void updateStatus_success() {
        User user = User.builder()
                .id(1L)
                .username("u1")
                .password("x")
                .role("USER")
                .createdAt(LocalDateTime.now())
                .build();

        Order order = Order.builder()
                .id(3L)
                .menuName("m")
                .quantity(1)
                .status(OrderStatus.CREATED)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(orderRepository.findByIdWithUser(3L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.updateStatus(3L, OrderStatus.COMPLETED);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("상태 변경 실패: 주문 없음")
    void updateStatus_orderNotFound_throws() {
        when(orderRepository.findByIdWithUser(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateStatus(100L, OrderStatus.COMPLETED))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
    }
}
