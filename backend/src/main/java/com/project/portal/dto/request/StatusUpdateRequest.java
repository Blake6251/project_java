package com.project.portal.dto.request;

import com.project.portal.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {

    @NotNull
    @Schema(description = "蹂寃쏀븷 二쇰Ц ?곹깭", example = "COMPLETED")
    private OrderStatus status;
}
