package com.project.portal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.project.portal.config.JwtUtil;
import com.project.portal.domain.User;
import com.project.portal.dto.request.LoginRequest;
import com.project.portal.dto.request.RegisterRequest;
import com.project.portal.dto.response.LoginResponse;
import com.project.portal.exception.CustomException;
import com.project.portal.exception.ErrorCode;
import com.project.portal.repository.UserRepository;
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

/** AuthService ?⑥쐞 ?뚯뒪??(Mockito). ?뚯썝媛?끒룸줈洹몄씤 ?깃났/?ㅽ뙣 ?쒕굹由ъ삤. */
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
    @DisplayName("Register success: new user")
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
    @DisplayName("?뚯썝媛???ㅽ뙣: 以묐났 ?ъ슜?먮챸")
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
    @DisplayName("濡쒓렇???깃났: JWT ?좏겙 諛섑솚")
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
    @DisplayName("濡쒓렇???ㅽ뙣: ?섎せ???먭꺽利앸챸")
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
    @DisplayName("Logout success: blacklist token")
    void logout_success() {
        String header = "Bearer test-token";
        when(jwtUtil.isTokenValid("test-token")).thenReturn(true);
        when(jwtUtil.getRemainingMillis("test-token")).thenReturn(30_000L);

        authService.logout(header);

        verify(tokenBlacklistService).blacklist("test-token", 30_000L);
    }

    @Test
    @DisplayName("濡쒓렇?꾩썐 ?ㅽ뙣: Authorization ?ㅻ뜑 ?뺤떇 ?ㅻ쪟")
    void logout_invalidHeader_throws() {
        assertThatThrownBy(() -> authService.logout("invalid"))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_INPUT);
    }
}
