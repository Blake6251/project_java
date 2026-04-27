package com.project.portal.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminStatsSummaryResponse {

    @Schema(description = "?곹깭蹂?二쇰Ц 吏묎퀎")
    private List<StatusCountResponse> statusCounts;
}
