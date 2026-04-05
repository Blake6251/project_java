package com.project.kiosk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminStatsSummaryResponse {

    @Schema(description = "상태별 주문 집계")
    private List<StatusCountResponse> statusCounts;
}
