package com.project.kiosk.dto.request;

import com.project.kiosk.domain.OrderStatus;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearchCondition {
    private OrderStatus status;
    private String menuName;
    private LocalDate startDate;
    private LocalDate endDate;
}
