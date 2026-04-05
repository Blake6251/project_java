package com.project.kiosk.controller;

import com.project.kiosk.dto.request.OrderRequest;
import com.project.kiosk.dto.request.OrderSearchCondition;
import com.project.kiosk.dto.request.StatusUpdateRequest;
import com.project.kiosk.dto.response.OrderResponse;
import com.project.kiosk.dto.response.PageResponse;
import com.project.kiosk.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "주문 생성", description = "인증된 사용자가 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(orderService.create(request, authentication.getName()));
    }

    @Operation(summary = "주문 전체 조회(관리자)", description = "관리자 권한으로 모든 주문을 조회합니다.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) com.project.kiosk.domain.OrderStatus status,
            @RequestParam(required = false) String menuName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        boolean hasSearch = status != null || (menuName != null && !menuName.isBlank()) || startDate != null || endDate != null;
        boolean hasPaging = page != null || size != null;
        if (!hasPaging && !hasSearch) {
            return ResponseEntity.ok(orderService.findAllForAdmin());
        }

        int pageNo = page == null ? 0 : page;
        int pageSize = size == null ? 10 : size;
        String[] sortParts = sort.split(",");
        String sortProperty = sortParts[0];
        Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(direction, sortProperty));

        OrderSearchCondition condition = new OrderSearchCondition();
        condition.setStatus(status);
        condition.setMenuName(menuName);
        condition.setStartDate(startDate);
        condition.setEndDate(endDate);

        PageResponse<OrderResponse> response = orderService.findAllForAdmin(condition, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 주문 목록", description = "로그인한 사용자 본인의 주문을 페이징 조회합니다.")
    @GetMapping("/mine")
    public ResponseEntity<PageResponse<OrderResponse>> findMine(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        String[] sortParts = sort.split(",");
        String sortProperty = sortParts[0];
        Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1]))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortProperty));
        return ResponseEntity.ok(orderService.findMineForUser(authentication.getName(), pageable));
    }

    @Operation(summary = "주문 단건 조회", description = "본인 주문 또는 관리자 권한으로 단건 주문을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        return ResponseEntity.ok(orderService.findById(id, authentication.getName(), isAdmin));
    }

    @Operation(summary = "주문 상태 변경(관리자)", description = "관리자 권한으로 주문 상태를 변경합니다.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }
}
