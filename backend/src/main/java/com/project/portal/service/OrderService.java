package com.project.kiosk.service;

import com.project.kiosk.domain.Order;
import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.domain.User;
import com.project.kiosk.dto.request.OrderSearchCondition;
import com.project.kiosk.dto.request.OrderRequest;
import com.project.kiosk.dto.response.OrderResponse;
import com.project.kiosk.dto.response.PageResponse;
import com.project.kiosk.event.OrderCreatedEvent;
import com.project.kiosk.event.OrderStatusChangedEvent;
import com.project.kiosk.exception.CustomException;
import com.project.kiosk.exception.ErrorCode;
import com.project.kiosk.repository.OrderRepository;
import com.project.kiosk.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @CacheEvict(value = "orders-admin-all", allEntries = true)
    public OrderResponse create(OrderRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Order order = Order.builder()
                .menuName(request.getMenuName())
                .quantity(request.getQuantity())
                .status(OrderStatus.CREATED)
                .user(user)
                .build();

        Order saved = orderRepository.save(order);
        OrderResponse response = toResponse(saved);
        eventPublisher.publishEvent(new OrderCreatedEvent(saved, response));
        return response;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "orders-admin-all")
    public List<OrderResponse> findAllForAdmin() {
        return orderRepository.findAllWithUser().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> findAllForAdmin(OrderSearchCondition condition, Pageable pageable) {
        Page<Order> page = orderRepository.searchOrders(condition, pageable);
        return PageResponse.<OrderResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .build();
    }

    /** 일반 사용자 본인 주문 목록(페이징) */
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> findMineForUser(String username, Pageable pageable) {
        Page<Order> page = orderRepository.findByUser_UsernameOrderByCreatedAtDesc(username, pageable);
        return PageResponse.<OrderResponse>builder()
                .content(page.getContent().stream().map(this::toResponse).toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .currentPage(page.getNumber())
                .build();
    }

    @Transactional(readOnly = true)
    public OrderResponse findById(Long id, String username, boolean isAdmin) {
        Order order = orderRepository.findByIdWithUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (!isAdmin && !order.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("해당 주문을 조회할 권한이 없습니다.");
        }

        return toResponse(order);
    }

    @Transactional
    @CacheEvict(value = "orders-admin-all", allEntries = true)
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findByIdWithUser(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        order.setStatus(status);
        eventPublisher.publishEvent(new OrderStatusChangedEvent(order));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .menuName(order.getMenuName())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .username(order.getUser().getUsername())
                .build();
    }
}
