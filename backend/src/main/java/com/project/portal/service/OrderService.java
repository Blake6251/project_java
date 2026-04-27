package com.project.portal.service;

import com.project.portal.domain.Order;
import com.project.portal.domain.OrderStatus;
import com.project.portal.domain.User;
import com.project.portal.dto.request.OrderSearchCondition;
import com.project.portal.dto.request.OrderRequest;
import com.project.portal.dto.response.OrderResponse;
import com.project.portal.dto.response.PageResponse;
import com.project.portal.event.OrderCreatedEvent;
import com.project.portal.event.OrderStatusChangedEvent;
import com.project.portal.exception.CustomException;
import com.project.portal.exception.ErrorCode;
import com.project.portal.repository.OrderRepository;
import com.project.portal.repository.UserRepository;
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

    /** ?쇰컲 ?ъ슜??蹂몄씤 二쇰Ц 紐⑸줉(?섏씠吏? */
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
            throw new AccessDeniedException("?대떦 二쇰Ц??議고쉶??沅뚰븳???놁뒿?덈떎.");
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
