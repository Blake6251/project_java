package com.project.portal.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Schema(description = "Username", example = "newuser")
    private String username;

    @NotBlank
    @Schema(description = "Password", example = "password1234")
    private String password;

    @NotBlank
    @Schema(description = "Role", example = "USER")
    private String role;
}
