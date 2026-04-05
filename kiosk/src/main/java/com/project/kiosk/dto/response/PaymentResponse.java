package com.project.kiosk.dto.response;

import com.project.kiosk.domain.OrderStatus;
import com.project.kiosk.domain.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse {

    @Schema(description = "결제 ID", example = "1")
    private Long id;
    @Schema(description = "주문 ID", example = "10")
    private Long orderId;
    @Schema(description = "주문자 아이디", example = "user1")
    private String username;
    @Schema(description = "결제 금액", example = "4500")
    private Integer amount;
    @Schema(description = "결제 상태", example = "CREATED")
    private PaymentStatus status;
    @Schema(description = "주문 상태", example = "CREATED")
    private OrderStatus orderStatus;
    @Schema(description = "결제 생성 시각")
    private LocalDateTime createdAt;
}
