package com.project.kiosk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.kiosk.config.JwtUtil;
import com.project.kiosk.domain.User;
import com.project.kiosk.dto.request.LoginRequest;
import com.project.kiosk.dto.request.RegisterRequest;
import com.project.kiosk.dto.response.LoginResponse;
import com.project.kiosk.exception.CustomException;
import com.project.kiosk.exception.ErrorCode;
import com.project.kiosk.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

/** AuthService 단위 테스트 (Mockito). 회원가입·로그인 성공/실패 시나리오. */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private LoginRateLimitService loginRateLimitService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("회원가입 성공: 신규 사용자 저장")
    void register_success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("pw");
        request.setRole("USER");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("pw")).thenReturn("encoded");

        authService.register(request);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패: 중복 사용자명")
    void register_duplicateUsername_throws() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("dup");
        request.setPassword("pw");
        request.setRole("USER");

        when(userRepository.existsByUsername("dup")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.DUPLICATE_USERNAME);
    }

    @Test
    @DisplayName("로그인 성공: JWT 토큰 반환")
    void login_success() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword("secret");

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "user1",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );

        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(loginRateLimitService.isBlocked("127.0.0.1")).thenReturn(false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtUtil.generateToken(eq("user1"), eq("ROLE_USER"))).thenReturn("jwt-token");

        LoginResponse response = authService.login(request, httpServletRequest);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getUsername()).isEqualTo("user1");
        assertThat(response.getRole()).isEqualTo("USER");
        verify(loginRateLimitService).clearFailures("127.0.0.1");
    }

    @Test
    @DisplayName("로그인 실패: 잘못된 자격증명")
    void login_badCredentials_throws() {
        LoginRequest request = new LoginRequest();
        request.setUsername("user1");
        request.setPassword("wrong");

        when(httpServletRequest.getHeader("X-Forwarded-For")).thenReturn(null);
        when(httpServletRequest.getHeader("X-Real-IP")).thenReturn(null);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(loginRateLimitService.isBlocked("127.0.0.1")).thenReturn(false);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        assertThatThrownBy(() -> authService.login(request, httpServletRequest))
                .isInstanceOf(BadCredentialsException.class);
        verify(loginRateLimitService).recordFailedAttempt("127.0.0.1");
    }

    @Test
    @DisplayName("로그아웃 성공: 블랙리스트 저장")
    void logout_success() {
        String header = "Bearer test-token";
        when(jwtUtil.isTokenValid("test-token")).thenReturn(true);
        when(jwtUtil.getRemainingMillis("test-token")).thenReturn(30_000L);

        authService.logout(header);

        verify(tokenBlacklistService).blacklist("test-token", 30_000L);
    }

    @Test
    @DisplayName("로그아웃 실패: Authorization 헤더 형식 오류")
    void logout_invalidHeader_throws() {
        assertThatThrownBy(() -> authService.logout("invalid"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INPUT);
    }
}
