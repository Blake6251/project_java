package com.project.portal.dto.request;

import com.project.portal.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentStatusUpdateRequest {

    @NotNull
    @Schema(description = "寃곗젣 ?곹깭", example = "PAID")
    private PaymentStatus status;
}
