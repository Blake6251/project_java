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
import com.project.portal.dto.request.OrderRequest;
import com.project.portal.dto.request.StatusUpdateRequest;
import com.project.portal.dto.response.OrderResponse;
import com.project.portal.dto.response.PageResponse;
import com.project.portal.exception.GlobalExceptionHandler;
import com.project.portal.service.OrderService;
import com.project.portal.service.TokenBlacklistService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

/** OrderController MockMvc ?뚯뒪?? ?몄쬆(401)쨌??븷蹂??멸?(403)쨌?뺤긽 ?묐떟. */
@WebMvcTest(controllers = OrderController.class)
@Import({SecurityConfig.class, CorsConfig.class, GlobalExceptionHandler.class, JwtFilter.class})
class OrderControllerTest {

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
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/orders ?깃났 (?몄쬆??")
    @WithMockUser(username = "user1", roles = "USER")
    void create_ok() throws Exception {
        OrderRequest body = new OrderRequest();
        body.setMenuName("?쇰뼹");
        body.setQuantity(1);

        OrderResponse res = OrderResponse.builder()
                .id(1L)
                .menuName("?쇰뼹")
                .quantity(1)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .username("user1")
                .build();

        when(orderService.create(any(OrderRequest.class), eq("user1"))).thenReturn(res);

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.menuName").value("?쇰뼹"));
    }

    @Test
    @DisplayName("POST /api/orders ?ㅽ뙣: 誘몄씤利?401)")
    void create_unauthorized() throws Exception {
        OrderRequest body = new OrderRequest();
        body.setMenuName("?쇰뼹");
        body.setQuantity(1);

        mockMvc.perform(
                        post("/api/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/orders ?ㅽ뙣: ?쇰컲 ?ъ슜??403)")
    @WithMockUser(username = "user1", roles = "USER")
    void findAll_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/orders")).andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/orders ?깃났: 愿由ъ옄")
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void findAll_okForAdmin() throws Exception {
        when(orderService.findAllForAdmin()).thenReturn(List.of());

        mockMvc.perform(get("/api/orders")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/orders ?섏씠吏?寃???깃났: 愿由ъ옄")
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void findAll_pagedAndFiltered_okForAdmin() throws Exception {
        PageResponse<OrderResponse> page = PageResponse.<OrderResponse>builder()
                .content(List.of())
                .totalElements(0)
                .totalPages(0)
                .currentPage(0)
                .build();
        when(orderService.findAllForAdmin(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/orders")
                        .param("page", "0")
                        .param("size", "5")
                        .param("status", "CREATED")
                        .param("menuName", "Americano"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage").value(0));
    }

    @Test
    @DisplayName("GET /api/orders/mine success: own paged orders")
    @WithMockUser(username = "user1", roles = "USER")
    void findMine_ok() throws Exception {
        PageResponse<OrderResponse> page = PageResponse.<OrderResponse>builder()
                .content(List.of(
                        OrderResponse.builder()
                                .id(1L)
                                .menuName("Americano")
                                .quantity(1)
                                .status(OrderStatus.CREATED)
                                .createdAt(LocalDateTime.now())
                                .username("user1")
                                .build()))
                .totalElements(1)
                .totalPages(1)
                .currentPage(0)
                .build();
        when(orderService.findMineForUser(eq("user1"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/orders/mine").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].username").value("user1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/orders/{id} ?깃났")
    @WithMockUser(username = "user1", roles = "USER")
    void findById_ok() throws Exception {
        OrderResponse res = OrderResponse.builder()
                .id(5L)
                .menuName("Americano")
                .quantity(2)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .username("user1")
                .build();

        when(orderService.findById(eq(5L), eq("user1"), eq(false))).thenReturn(res);

        mockMvc.perform(get("/api/orders/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    @DisplayName("GET /api/orders/{id} ?ㅽ뙣: ?쒕퉬???묎렐 嫄곕?(403)")
    @WithMockUser(username = "user2", roles = "USER")
    void findById_accessDeniedFromService() throws Exception {
        when(orderService.findById(eq(5L), eq("user2"), eq(false)))
                .thenThrow(new AccessDeniedException("?대떦 二쇰Ц??議고쉶??沅뚰븳???놁뒿?덈떎."));

        mockMvc.perform(get("/api/orders/5"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    @DisplayName("GET /api/orders/{id} ?ㅽ뙣: 誘몄씤利?401)")
    void findById_unauthorized() throws Exception {
        mockMvc.perform(get("/api/orders/5")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/status ?깃났: 愿由ъ옄")
    @WithMockUser(username = "admin1", roles = "ADMIN")
    void updateStatus_ok() throws Exception {
        StatusUpdateRequest body = new StatusUpdateRequest();
        body.setStatus(OrderStatus.COMPLETED);

        OrderResponse res = OrderResponse.builder()
                .id(1L)
                .menuName("m")
                .quantity(1)
                .status(OrderStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .username("u")
                .build();

        when(orderService.updateStatus(eq(1L), eq(OrderStatus.COMPLETED))).thenReturn(res);

        mockMvc.perform(
                        patch("/api/orders/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("PATCH /api/orders/{id}/status ?ㅽ뙣: ?쇰컲 ?ъ슜??403)")
    @WithMockUser(username = "user1", roles = "USER")
    void updateStatus_forbidden() throws Exception {
        StatusUpdateRequest body = new StatusUpdateRequest();
        body.setStatus(OrderStatus.COMPLETED);

        mockMvc.perform(
                        patch("/api/orders/1/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden());
    }
}
