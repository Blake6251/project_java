package com.project.portal.dto.request;

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
    @Schema(description = "Menu name", example = "Americano")
    private String menuName;

    @NotNull
    @Positive
    @Schema(description = "Quantity", example = "2")
    private Integer quantity;
}
