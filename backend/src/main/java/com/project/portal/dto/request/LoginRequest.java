package com.project.portal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank
    @Schema(description = "Login username", example = "user1")
    private String username;

    @NotBlank
    @Schema(description = "Password", example = "password1234")
    private String password;
}
