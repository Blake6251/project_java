package com.project.portal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.project.portal.domain.Order;
import com.project.portal.domain.OrderStatus;
import com.project.portal.domain.User;
import com.project.portal.dto.request.OrderRequest;
import com.project.portal.dto.response.OrderResponse;
import com.project.portal.exception.CustomException;
import com.project.portal.exception.ErrorCode;
import com.project.portal.repository.OrderRepository;
import com.project.portal.repository.UserRepository;
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

/** OrderService ?⑥쐞 ?뚯뒪??(Mockito). 二쇰Ц ?앹꽦쨌議고쉶쨌?곹깭 蹂寃?諛?沅뚰븳 ?덉쇅. */
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
    @DisplayName("二쇰Ц ?앹꽦 ?깃났: ?????WebSocket 釉뚮줈?쒖틦?ㅽ듃")
    void create_success() {
        OrderRequest request = new OrderRequest();
        request.setMenuName("Americano");
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
                .menuName("Americano")
                .quantity(2)
                .status(OrderStatus.CREATED)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        OrderResponse result = orderService.create(request, "user1");

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getMenuName()).isEqualTo("Americano");
    }

    @Test
    @DisplayName("二쇰Ц ?앹꽦 ?ㅽ뙣: ?ъ슜???놁쓬")
    void create_userNotFound_throws() {
        OrderRequest request = new OrderRequest();
        request.setMenuName("?쇰뼹");
        request.setQuantity(1);

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.create(request, "ghost"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.USER_NOT_FOUND);
    }

    @Test
    @DisplayName("?④굔 議고쉶 ?깃났: 蹂몄씤 二쇰Ц")
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
                .menuName("肄쒕뱶釉뚮（")
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
    @DisplayName("Find by id success: admin can access others")
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
                .menuName("Espresso")
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
    @DisplayName("?④굔 議고쉶 ?ㅽ뙣: ???二쇰Ц (?쇰컲 ?ъ슜??")
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
                .menuName("Tea")
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
    @DisplayName("?④굔 議고쉶 ?ㅽ뙣: 二쇰Ц ?놁쓬")
    void findById_orderNotFound_throws() {
        when(orderRepository.findByIdWithUser(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findById(99L, "user1", false))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
    }

    @Test
    @DisplayName("愿由ъ옄 ?꾩껜 紐⑸줉")
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
    @DisplayName("?곹깭 蹂寃??깃났")
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
    @DisplayName("?곹깭 蹂寃??ㅽ뙣: 二쇰Ц ?놁쓬")
    void updateStatus_orderNotFound_throws() {
        when(orderRepository.findByIdWithUser(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateStatus(100L, OrderStatus.COMPLETED))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ORDER_NOT_FOUND);
    }
}
