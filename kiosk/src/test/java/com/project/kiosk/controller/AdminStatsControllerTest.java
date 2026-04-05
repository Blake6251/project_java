package com.project.kiosk.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.project.kiosk.config.JwtFilter;
import com.project.kiosk.config.JwtUtil;
import com.project.kiosk.config.CorsConfig;
import com.project.kiosk.config.SecurityConfig;
import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.dto.response.AdminStatsSummaryResponse;
import com.project.kiosk.dto.response.DailyStatsResponse;
import com.project.kiosk.dto.response.StatusCountResponse;
import com.project.kiosk.exception.GlobalExceptionHandler;
import com.project.kiosk.service.AdminStatsService;
import com.project.kiosk.service.TokenBlacklistService;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AdminStatsController.class)
@Import({SecurityConfig.class, CorsConfig.class, GlobalExceptionHandler.class, JwtFilter.class})
class AdminStatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @MockBean
    private AdminStatsService adminStatsService;

    @Test
    @DisplayName("관리자 일자별 통계 조회 성공")
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void daily_admin_ok() throws Exception {
        when(adminStatsService.getDailyStats()).thenReturn(List.of(
                DailyStatsResponse.builder()
                        .date(LocalDate.of(2026, 3, 31))
                        .orderCount(3L)
                        .totalQuantity(7L)
                        .build()
        ));

        mockMvc.perform(get("/api/admin/stats/daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderCount").value(3));
    }

    @Test
    @DisplayName("일반 유저 통계 조회 실패 403")
    @WithMockUser(username = "user1", roles = "USER")
    void daily_user_forbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats/daily"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 요약 통계 조회 성공")
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void summary_admin_ok() throws Exception {
        when(adminStatsService.getSummary()).thenReturn(
                AdminStatsSummaryResponse.builder()
                        .statusCounts(List.of(
                                StatusCountResponse.builder().status(OrderStatus.CREATED).count(2L).build()
                        ))
                        .build()
        );

        mockMvc.perform(get("/api/admin/stats/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCounts[0].status").value("CREATED"));
    }
}
