package com.project.kiosk.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank
    @Schema(description = "로그인 아이디", example = "user1")
    private String username;

    @NotBlank
    @Schema(description = "비밀번호", example = "password1234")
    private String password;
}
