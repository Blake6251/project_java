package com.project.portal.event;

import com.project.portal.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStatusChangedEvent {
    private Order order;
}
