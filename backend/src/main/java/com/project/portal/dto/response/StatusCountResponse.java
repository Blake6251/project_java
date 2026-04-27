package com.project.portal.dto.response;

import com.project.portal.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StatusCountResponse {

    @Schema(description = "二쇰Ц ?곹깭", example = "CREATED")
    private OrderStatus status;

    @Schema(description = "?곹깭蹂?二쇰Ц 嫄댁닔", example = "5")
    private Long count;
}
