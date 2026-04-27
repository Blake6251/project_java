package com.project.portal.repository;

import static com.project.portal.domain.QOrder.order;

import com.project.portal.domain.Order;
import com.project.portal.domain.OrderStatus;
import com.project.portal.dto.request.OrderSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@RequiredArgsConstructor
public class QuerydslOrderRepositoryImpl implements QuerydslOrderRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> searchOrders(OrderSearchCondition condition, Pageable pageable) {
        var baseQuery = queryFactory
                .selectFrom(order)
                .leftJoin(order.user).fetchJoin()
                .where(
                        statusEq(condition.getStatus()),
                        menuNameContains(condition.getMenuName()),
                        createdAtGoe(condition.getStartDate()),
                        createdAtLoe(condition.getEndDate())
                );

        applySort(baseQuery, pageable);
        List<Order> content = baseQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(order.count())
                .from(order)
                .where(
                        statusEq(condition.getStatus()),
                        menuNameContains(condition.getMenuName()),
                        createdAtGoe(condition.getStartDate()),
                        createdAtLoe(condition.getEndDate())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private void applySort(com.querydsl.jpa.impl.JPAQuery<Order> query, Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            query.orderBy(order.createdAt.desc());
            return;
        }
        for (Sort.Order sortOrder : pageable.getSort()) {
            boolean asc = sortOrder.isAscending();
            switch (sortOrder.getProperty()) {
                case "menuName" -> query.orderBy(asc ? order.menuName.asc() : order.menuName.desc());
                case "quantity" -> query.orderBy(asc ? order.quantity.asc() : order.quantity.desc());
                case "status" -> query.orderBy(asc ? order.status.asc() : order.status.desc());
                case "createdAt" -> query.orderBy(asc ? order.createdAt.asc() : order.createdAt.desc());
                default -> query.orderBy(order.createdAt.desc());
            }
        }
    }

    private BooleanExpression statusEq(OrderStatus status) {
        return status == null ? null : order.status.eq(status);
    }

    private BooleanExpression menuNameContains(String menuName) {
        return (menuName == null || menuName.isBlank()) ? null : order.menuName.containsIgnoreCase(menuName);
    }

    private BooleanExpression createdAtGoe(LocalDate startDate) {
        return startDate == null ? null : order.createdAt.goe(startDate.atStartOfDay());
    }

    private BooleanExpression createdAtLoe(LocalDate endDate) {
        if (endDate == null) {
            return null;
        }
        LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
        return order.createdAt.loe(endOfDay);
    }
}
