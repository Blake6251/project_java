package com.project.kiosk.dto.response;

import com.project.kiosk.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StatusCountResponse {

    @Schema(description = "주문 상태", example = "CREATED")
    private OrderStatus status;

    @Schema(description = "상태별 주문 건수", example = "5")
    private Long count;
}
