package com.project.kiosk.dto.request;

import com.project.kiosk.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StatusUpdateRequest {

    @NotNull
    @Schema(description = "변경할 주문 상태", example = "COMPLETED")
    private OrderStatus status;
}
