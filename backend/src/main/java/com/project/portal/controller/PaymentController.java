package com.project.kiosk.controller;

import com.project.kiosk.dto.request.PaymentCreateRequest;
import com.project.kiosk.dto.request.PaymentStatusUpdateRequest;
import com.project.kiosk.dto.response.PaymentResponse;
import com.project.kiosk.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "결제 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "결제 생성", description = "주문에 대한 결제를 생성합니다.")
    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @Valid @RequestBody PaymentCreateRequest request,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return ResponseEntity.ok(paymentService.create(request, authentication.getName(), isAdmin));
    }

    @Operation(summary = "결제 조회", description = "본인 결제 또는 관리자 권한으로 결제를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return ResponseEntity.ok(paymentService.findById(id, authentication.getName(), isAdmin));
    }

    @Operation(summary = "결제 상태 변경(관리자)", description = "관리자 권한으로 결제 상태를 변경합니다.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(paymentService.updateStatus(id, request.getStatus()));
    }
}
