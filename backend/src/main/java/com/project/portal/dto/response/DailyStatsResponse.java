package com.project.portal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DailyStatsResponse {

    @Schema(description = "吏묎퀎 ?좎쭨", example = "2026-03-31")
    private LocalDate date;

    @Schema(description = "二쇰Ц 嫄댁닔", example = "12")
    private Long orderCount;

    @Schema(description = "珥?二쇰Ц ?섎웾", example = "28")
    private Long totalQuantity;
}
