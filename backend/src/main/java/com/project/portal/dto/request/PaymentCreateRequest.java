package com.project.kiosk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCreateRequest {

    @NotNull
    @Schema(description = "결제 대상 주문 ID", example = "1")
    private Long orderId;

    @NotNull
    @Positive
    @Schema(description = "결제 금액", example = "4500")
    private Integer amount;
}
