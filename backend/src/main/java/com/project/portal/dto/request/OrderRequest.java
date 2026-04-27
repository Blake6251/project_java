package com.project.kiosk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {

    @NotBlank
    @Schema(description = "메뉴 이름", example = "아메리카노")
    private String menuName;

    @NotNull
    @Positive
    @Schema(description = "수량", example = "2")
    private Integer quantity;
}
