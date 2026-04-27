package com.project.portal.scheduler;

import com.project.portal.domain.Order;
import com.project.portal.domain.OrderStatus;
import com.project.portal.repository.OrderRepository;
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

    // 留ㅼ씪 ?먯젙: 24?쒓컙 吏??CREATED 二쇰Ц???먮룞 痍⑥냼?쒕떎.
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

    // 留?1?쒓컙: IN_PROGRESS 二쇰Ц 嫄댁닔瑜?濡쒓렇濡?異쒕젰?쒕떎.
    @Scheduled(cron = "0 0 * * * *")
    @Transactional(readOnly = true)
    public void logInProgressOrdersCount() {
        long count = orderRepository.countByStatus(OrderStatus.IN_PROGRESS);
        log.info("Scheduler IN_PROGRESS orders count={}", count);
    }
}
