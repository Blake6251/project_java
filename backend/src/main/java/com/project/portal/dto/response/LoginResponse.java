package com.project.portal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "JWT ?≪꽭???좏겙")
    private String token;

    @Schema(description = "濡쒓렇???ъ슜?먮챸")
    private String username;

    @Schema(description = "沅뚰븳 (USER ?먮뒗 ADMIN, ROLE_ ?묐몢 ?놁쓬)")
    private String role;
}
