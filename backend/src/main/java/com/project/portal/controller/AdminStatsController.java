package com.project.portal.controller;

import com.project.portal.dto.response.AdminStatsSummaryResponse;
import com.project.portal.dto.response.DailyStatsResponse;
import com.project.portal.service.AdminStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
@Tag(name = "AdminStats", description = "愿由ъ옄 ?듦퀎 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @Operation(summary = "?쇱옄蹂?二쇰Ц ?듦퀎", description = "?좎쭨蹂?二쇰Ц 嫄댁닔/珥??섎웾??議고쉶?⑸땲??")
    @GetMapping("/daily")
    public ResponseEntity<List<DailyStatsResponse>> daily() {
        return ResponseEntity.ok(adminStatsService.getDailyStats());
    }

    @Operation(summary = "二쇰Ц ?곹깭 ?붿빟 ?듦퀎", description = "二쇰Ц ?곹깭蹂?嫄댁닔瑜?議고쉶?⑸땲??")
    @GetMapping("/summary")
    public ResponseEntity<AdminStatsSummaryResponse> summary() {
        return ResponseEntity.ok(adminStatsService.getSummary());
    }
}
