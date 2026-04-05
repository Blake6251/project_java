package com.project.kiosk.event;

import com.project.kiosk.domain.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderStatusChangedEvent {
    private Order order;
}
