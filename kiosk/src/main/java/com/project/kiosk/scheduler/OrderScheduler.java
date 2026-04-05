package com.project.kiosk.scheduler;

import com.project.kiosk.domain.Order;
import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OrderRepository orderRepository;

    // 매일 자정: 24시간 지난 CREATED 주문을 자동 취소한다.
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cancelOldCreatedOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        List<Order> oldCreatedOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.CREATED, threshold);
        for (Order order : oldCreatedOrders) {
            order.setStatus(OrderStatus.CANCELLED);
        }
        if (!oldCreatedOrders.isEmpty()) {
            log.info("Scheduler cancelled {} old CREATED orders", oldCreatedOrders.size());
        }
    }

    // 매 1시간: IN_PROGRESS 주문 건수를 로그로 출력한다.
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void logInProgressOrdersCount() {
        long count = orderRepository.countByStatus(OrderStatus.IN_PROGRESS);
        log.info("Scheduler IN_PROGRESS orders count={}", count);
    }
}
