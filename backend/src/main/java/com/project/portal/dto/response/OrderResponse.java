package com.project.portal.dto.response;

import com.project.portal.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {

    @Schema(description = "Order ID", example = "1")
    private Long id;
    @Schema(description = "Menu name", example = "Americano")
    private String menuName;
    @Schema(description = "Order quantity", example = "2")
    private Integer quantity;
    @Schema(description = "Order status", example = "CREATED")
    private OrderStatus status;
    @Schema(description = "Order created time")
    private LocalDateTime createdAt;
    @Schema(description = "Order username", example = "user1")
    private String username;
}
