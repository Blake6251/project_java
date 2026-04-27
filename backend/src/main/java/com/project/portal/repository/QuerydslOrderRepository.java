package com.project.portal.repository;

import com.project.portal.domain.Order;
import com.project.portal.dto.request.OrderSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuerydslOrderRepository {

    Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable);
}
