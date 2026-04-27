package com.project.kiosk.dto.request;

import com.project.kiosk.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusUpdateRequest {

    @NotNull
    @Schema(description = "결제 상태", example = "PAID")
    private PaymentStatus status;
}
