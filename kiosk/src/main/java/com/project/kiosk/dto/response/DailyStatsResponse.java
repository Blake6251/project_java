package com.project.kiosk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DailyStatsResponse {

    @Schema(description = "집계 날짜", example = "2026-03-31")
    private LocalDate date;

    @Schema(description = "주문 건수", example = "12")
    private Long orderCount;

    @Schema(description = "총 주문 수량", example = "28")
    private Long totalQuantity;
}
