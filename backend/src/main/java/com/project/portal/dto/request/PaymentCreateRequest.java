package com.project.portal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCreateRequest {

    @NotNull
    @Schema(description = "寃곗젣 ???二쇰Ц ID", example = "1")
    private Long orderId;

    @NotNull
    @Positive
    @Schema(description = "寃곗젣 湲덉븸", example = "4500")
    private Integer amount;
}
