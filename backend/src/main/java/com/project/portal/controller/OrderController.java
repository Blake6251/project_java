package com.project.portal.controller;

import com.project.portal.dto.request.OrderRequest;
import com.project.portal.dto.request.OrderSearchCondition;
import com.project.portal.dto.request.StatusUpdateRequest;
import com.project.portal.dto.response.OrderResponse;
import com.project.portal.dto.response.PageResponse;
import com.project.portal.service.OrderService;
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
@Tag(name = "Order", description = "二쇰Ц API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "二쇰Ц ?앹꽦", description = "?몄쬆???ъ슜?먭? 二쇰Ц???앹꽦?⑸땲??")
    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(orderService.create(request, authentication.getName()));
    }

    @Operation(summary = "二쇰Ц ?꾩껜 議고쉶(愿由ъ옄)", description = "愿由ъ옄 沅뚰븳?쇰줈 紐⑤뱺 二쇰Ц??議고쉶?⑸땲??")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "createdAt,desc") String sort,
            @RequestParam(required = false) com.project.portal.domain.OrderStatus status,
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

    @Operation(summary = "??二쇰Ц 紐⑸줉", description = "濡쒓렇?명븳 ?ъ슜??蹂몄씤??二쇰Ц???섏씠吏?議고쉶?⑸땲??")
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

    @Operation(summary = "二쇰Ц ?④굔 議고쉶", description = "蹂몄씤 二쇰Ц ?먮뒗 愿由ъ옄 沅뚰븳?쇰줈 ?④굔 二쇰Ц??議고쉶?⑸땲??")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        return ResponseEntity.ok(orderService.findById(id, authentication.getName(), isAdmin));
    }

    @Operation(summary = "二쇰Ц ?곹깭 蹂寃?愿由ъ옄)", description = "愿由ъ옄 沅뚰븳?쇰줈 二쇰Ц ?곹깭瑜?蹂寃쏀빀?덈떎.")
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        return ResponseEntity.ok(orderService.updateStatus(id, request.getStatus()));
    }
}
