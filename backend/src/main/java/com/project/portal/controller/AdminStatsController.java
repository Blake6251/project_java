package com.project.kiosk.controller;

import com.project.kiosk.dto.response.AdminStatsSummaryResponse;
import com.project.kiosk.dto.response.DailyStatsResponse;
import com.project.kiosk.service.AdminStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Tag(name = "AdminStats", description = "관리자 통계 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @Operation(summary = "일자별 주문 통계", description = "날짜별 주문 건수/총 수량을 조회합니다.")
    @GetMapping("/daily")
    public ResponseEntity<List<DailyStatsResponse>> daily() {
        return ResponseEntity.ok(adminStatsService.getDailyStats());
    }

    @Operation(summary = "주문 상태 요약 통계", description = "주문 상태별 건수를 조회합니다.")
    @GetMapping("/summary")
    public ResponseEntity<AdminStatsSummaryResponse> summary() {
        return ResponseEntity.ok(adminStatsService.getSummary());
    }
}
