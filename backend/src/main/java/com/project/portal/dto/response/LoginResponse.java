package com.project.kiosk.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    @Schema(description = "JWT 액세스 토큰")
    private String token;

    @Schema(description = "로그인 사용자명")
    private String username;

    @Schema(description = "권한 (USER 또는 ADMIN, ROLE_ 접두 없음)")
    private String role;
}
