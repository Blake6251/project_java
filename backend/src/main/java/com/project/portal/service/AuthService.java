package com.project.kiosk.service;

import com.project.kiosk.config.JwtUtil;
import com.project.kiosk.domain.User;
import com.project.kiosk.dto.request.LoginRequest;
import com.project.kiosk.dto.request.RegisterRequest;
import com.project.kiosk.dto.response.LoginResponse;
import com.project.kiosk.exception.CustomException;
import com.project.kiosk.exception.ErrorCode;
import com.project.kiosk.config.ClientIpResolver;
import com.project.kiosk.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;
    private final LoginRateLimitService loginRateLimitService;

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = ClientIpResolver.resolve(httpRequest);
        if (loginRateLimitService.isBlocked(clientIp)) {
            throw new CustomException(ErrorCode.LOGIN_ATTEMPTS_EXCEEDED);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            loginRateLimitService.clearFailures(clientIp);

            String roleAuthority = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("ROLE_USER");

            String token = jwtUtil.generateToken(request.getUsername(), roleAuthority);
            String roleShort = roleAuthority.startsWith("ROLE_")
                    ? roleAuthority.substring("ROLE_".length())
                    : roleAuthority;
            return new LoginResponse(token, request.getUsername(), roleShort);
        } catch (AuthenticationException e) {
            loginRateLimitService.recordFailedAttempt(clientIp);
            throw e;
        }
    }

    public void logout(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.INVALID_INPUT, "Authorization 헤더 형식이 올바르지 않습니다.");
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        long remainingMillis = jwtUtil.getRemainingMillis(token);
        if (remainingMillis <= 0) {
            throw new CustomException(ErrorCode.UNAUTHORIZED, "만료된 토큰입니다.");
        }

        tokenBlacklistService.blacklist(token, remainingMillis);
    }
}
