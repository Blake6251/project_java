package com.project.portal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.project.portal.domain.OrderStatus;
import com.project.portal.dto.response.AdminStatsSummaryResponse;
import com.project.portal.dto.response.DailyStatsResponse;
import com.project.portal.repository.OrderRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminStatsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private AdminStatsService adminStatsService;

    @Test
    @DisplayName("?쇱옄蹂??듦퀎 議고쉶")
    void getDailyStats_success() {
        when(orderRepository.findDailyStats()).thenReturn(List.<Object[]>of(
                new Object[]{LocalDate.of(2026, 3, 31), 3L, 7L}
        ));

        List<DailyStatsResponse> result = adminStatsService.getDailyStats();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getOrderCount()).isEqualTo(3L);
        assertThat(result.get(0).getTotalQuantity()).isEqualTo(7L);
    }

    @Test
    @DisplayName("?곹깭蹂??듦퀎 議고쉶")
    void getSummary_success() {
        when(orderRepository.countByStatus()).thenReturn(List.of(
                new Object[]{OrderStatus.CREATED, 2L},
                new Object[]{OrderStatus.COMPLETED, 1L}
        ));

        AdminStatsSummaryResponse result = adminStatsService.getSummary();

        assertThat(result.getStatusCounts()).hasSize(2);
        assertThat(result.getStatusCounts().get(0).getStatus()).isEqualTo(OrderStatus.CREATED);
    }
}
