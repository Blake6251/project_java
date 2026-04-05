package com.project.kiosk.repository;

import com.project.kiosk.domain.Order;
import com.project.kiosk.dto.request.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuerydslOrderRepository {

    Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);
}
