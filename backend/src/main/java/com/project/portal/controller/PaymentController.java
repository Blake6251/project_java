package com.project.portal.controller;

import com.project.portal.dto.request.PaymentCreateRequest;
import com.project.portal.dto.request.PaymentStatusUpdateRequest;
import com.project.portal.dto.response.PaymentResponse;
import com.project.portal.service.PaymentService;
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
@Tag(name = "Payment", description = "寃곗젣 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "寃곗젣 ?앹꽦", description = "二쇰Ц?????寃곗젣瑜??앹꽦?⑸땲??")
    @PostMapping
    public ResponseEntity<PaymentResponse> create(
            @Valid @RequestBody PaymentCreateRequest request,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return ResponseEntity.ok(paymentService.create(request, authentication.getName(), isAdmin));
    }

    @Operation(summary = "寃곗젣 議고쉶", description = "蹂몄씤 寃곗젣 ?먮뒗 愿由ъ옄 沅뚰븳?쇰줈 寃곗젣瑜?議고쉶?⑸땲??")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return ResponseEntity.ok(paymentService.findById(id, authentication.getName(), isAdmin));
    }

    @Operation(summary = "寃곗젣 ?곹깭 蹂寃?愿由ъ옄)", description = "愿由ъ옄 沅뚰븳?쇰줈 寃곗젣 ?곹깭瑜?蹂寃쏀빀?덈떎.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody PaymentStatusUpdateRequest request
    ) {
        return ResponseEntity.ok(paymentService.updateStatus(id, request.getStatus()));
    }
}
