package com.project.portal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.portal.config.JwtFilter;
import com.project.portal.config.JwtUtil;
import com.project.portal.config.CorsConfig;
import com.project.portal.config.SecurityConfig;
import com.project.portal.domain.OrderStatus;
import com.project.portal.domain.PaymentStatus;
import com.project.portal.dto.request.PaymentCreateRequest;
import com.project.portal.dto.request.PaymentStatusUpdateRequest;
import com.project.portal.dto.response.PaymentResponse;
import com.project.portal.exception.GlobalExceptionHandler;
import com.project.portal.service.PaymentService;
import com.project.portal.service.TokenBlacklistService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PaymentController.class)
@Import({SecurityConfig.class, CorsConfig.class, GlobalExceptionHandler.class, JwtFilter.class})
class PaymentControllerTest {

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
    private PaymentService paymentService;

    @Test
    @DisplayName("POST /api/payments ?깃났")
    @WithMockUser(username = "user1", roles = "USER")
    void create_ok() throws Exception {
        PaymentCreateRequest req = new PaymentCreateRequest();
        req.setOrderId(10L);
        req.setAmount(4500);

        PaymentResponse res = PaymentResponse.builder()
                .id(1L)
                .orderId(10L)
                .username("user1")
                .amount(4500)
                .status(PaymentStatus.CREATED)
                .orderStatus(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.create(any(PaymentCreateRequest.class), eq("user1"), eq(false))).thenReturn(res);

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @DisplayName("GET /api/payments/{id} 誘몄씤利??ㅽ뙣")
    void findById_unauthorized() throws Exception {
        mockMvc.perform(get("/api/payments/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/payments/{id}/status 愿由ъ옄 ?깃났")
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void updateStatus_admin_ok() throws Exception {
        PaymentStatusUpdateRequest req = new PaymentStatusUpdateRequest();
        req.setStatus(PaymentStatus.PAID);

        PaymentResponse res = PaymentResponse.builder()
                .id(1L)
                .orderId(10L)
                .username("user1")
                .amount(4500)
                .status(PaymentStatus.PAID)
                .orderStatus(OrderStatus.IN_PROGRESS)
                .createdAt(LocalDateTime.now())
                .build();

        when(paymentService.updateStatus(1L, PaymentStatus.PAID)).thenReturn(res);

        mockMvc.perform(patch("/api/payments/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus").value("IN_PROGRESS"));
    }
}
