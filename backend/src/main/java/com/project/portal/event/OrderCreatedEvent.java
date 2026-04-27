package com.project.portal.event;

import com.project.portal.domain.Order;
import com.project.portal.dto.response.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreatedEvent {
    private Order order;
    private OrderResponse response;
}
