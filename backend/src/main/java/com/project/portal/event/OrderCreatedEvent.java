package com.project.kiosk.event;

import com.project.kiosk.domain.Order;
import com.project.kiosk.dto.response.OrderResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderCreatedEvent {
    private Order order;
    private OrderResponse response;
}
