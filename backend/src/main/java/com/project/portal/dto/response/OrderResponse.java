package com.project.kiosk.dto.response;

import com.project.kiosk.domain.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {

    @Schema(description = "주문 ID", example = "1")
    private Long id;
    @Schema(description = "메뉴 이름", example = "아메리카노")
    private String menuName;
    @Schema(description = "주문 수량", example = "2")
    private Integer quantity;
    @Schema(description = "주문 상태", example = "CREATED")
    private OrderStatus status;
    @Schema(description = "주문 생성 시각")
    private LocalDateTime createdAt;
    @Schema(description = "주문자 아이디", example = "user1")
    private String username;
}
