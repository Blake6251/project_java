package com.project.portal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.portal.config.CorsConfig;
import com.project.portal.config.JwtFilter;
import com.project.portal.config.JwtUtil;
import com.project.portal.config.SecurityConfig;
import com.project.portal.dto.request.LoginRequest;
import com.project.portal.dto.request.RegisterRequest;
import com.project.portal.dto.response.LoginResponse;
import com.project.portal.exception.CustomException;
import com.project.portal.exception.ErrorCode;
import com.project.portal.exception.GlobalExceptionHandler;
import com.project.portal.service.AuthService;
import com.project.portal.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/** AuthController MockMvc ?뚯뒪?? SecurityConfig쨌JwtFilter ?ы븿, 寃利씲룸퉬利덈땲???덉쇅 ?묐떟. */
@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, CorsConfig.class, GlobalExceptionHandler.class, JwtFilter.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @MockBean
    private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/register ?깃났")
    void register_ok() throws Exception {
        RegisterRequest body = new RegisterRequest();
        body.setUsername("newuser");
        body.setPassword("pw1234");
        body.setRole("USER");

        doNothing().when(authService).register(any(RegisterRequest.class));

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Register success"));
    }

    @Test
    @DisplayName("POST /api/auth/register ?ㅽ뙣: 寃利??ㅻ쪟(400)")
    void register_validation_badRequest() throws Exception {
        RegisterRequest body = new RegisterRequest();
        body.setUsername("");
        body.setPassword("pw");
        body.setRole("USER");

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /api/auth/register ?ㅽ뙣: 以묐났 ?ъ슜?먮챸(400)")
    void register_duplicate_conflict() throws Exception {
        RegisterRequest body = new RegisterRequest();
        body.setUsername("dup");
        body.setPassword("pw");
        body.setRole("USER");

        doThrow(new CustomException(ErrorCode.DUPLICATE_USERNAME))
                .when(authService)
                .register(any(RegisterRequest.class));

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATE_USERNAME.getMessage()));
    }

    @Test
    @DisplayName("POST /api/auth/login ?깃났")
    void login_ok() throws Exception {
        LoginRequest body = new LoginRequest();
        body.setUsername("user1");
        body.setPassword("secret");

        when(authService.login(any(LoginRequest.class), any(HttpServletRequest.class)))
                .thenReturn(new LoginResponse("test-jwt", "user1", "USER"));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt"));
    }

    @Test
    @DisplayName("POST /api/auth/login ?ㅽ뙣: 寃利??ㅻ쪟(400)")
    void login_validation_badRequest() throws Exception {
        LoginRequest body = new LoginRequest();
        body.setUsername("");
        body.setPassword("x");

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /api/auth/login ?ㅽ뙣: ?몄쬆 ?ㅽ뙣(401)")
    void login_badCredentials_unauthorized() throws Exception {
        LoginRequest body = new LoginRequest();
        body.setUsername("user1");
        body.setPassword("wrong");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authService)
                .login(any(LoginRequest.class), any(HttpServletRequest.class));

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("POST /api/auth/logout ?깃났")
    void logout_ok() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Logout success"));
    }
}
