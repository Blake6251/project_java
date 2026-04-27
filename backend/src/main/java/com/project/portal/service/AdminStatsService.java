package com.project.kiosk.service;

import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.dto.response.AdminStatsSummaryResponse;
import com.project.kiosk.dto.response.DailyStatsResponse;
import com.project.kiosk.dto.response.StatusCountResponse;
import com.project.kiosk.repository.OrderRepository;
import java.time.LocalDate;
import java.sql.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<DailyStatsResponse> getDailyStats() {
        // JPQL 집계 결과(Object[])를 DTO로 변환한다.
        return orderRepository.findDailyStats().stream()
                .map(row -> DailyStatsResponse.builder()
                        .date(toLocalDate(row[0]))
                        .orderCount((Long) row[1])
                        .totalQuantity(((Number) row[2]).longValue())
                        .build())
                .toList();
    }

    private LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate localDate) {
            return localDate;
        }
        if (value instanceof Date sqlDate) {
            return sqlDate.toLocalDate();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public AdminStatsSummaryResponse getSummary() {
        List<StatusCountResponse> statusCounts = orderRepository.countByStatus().stream()
                .map(row -> StatusCountResponse.builder()
                        .status((OrderStatus) row[0])
                        .count((Long) row[1])
                        .build())
                .toList();

        return AdminStatsSummaryResponse.builder()
                .statusCounts(statusCounts)
                .build();
    }
}
