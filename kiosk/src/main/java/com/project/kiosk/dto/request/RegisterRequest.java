package com.project.kiosk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Schema(description = "회원 아이디", example = "newuser")
    private String username;

    @NotBlank
    @Schema(description = "비밀번호", example = "password1234")
    private String password;

    @NotBlank
    @Schema(description = "권한", example = "USER")
    private String role;
}
