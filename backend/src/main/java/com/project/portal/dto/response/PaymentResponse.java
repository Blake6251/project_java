package com.project.portal.dto.response;

import com.project.portal.domain.OrderStatus;
import com.project.portal.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse {

    @Schema(description = "Payment ID", example = "1")
    private Long id;
    @Schema(description = "Order ID", example = "10")
    private Long orderId;
    @Schema(description = "Order username", example = "user1")
    private String username;
    @Schema(description = "Payment amount", example = "4500")
    private Integer amount;
    @Schema(description = "Payment status", example = "CREATED")
    private PaymentStatus status;
    @Schema(description = "Order status", example = "CREATED")
    private OrderStatus orderStatus;
    @Schema(description = "Payment created time")
    private LocalDateTime createdAt;
}
