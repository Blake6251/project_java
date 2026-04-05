package com.project.kiosk.repository;

import com.project.kiosk.domain.DailyOrderStat;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyOrderStatRepository extends JpaRepository<DailyOrderStat, Long> {
    Optional<DailyOrderStat> findByStatDate(LocalDate statDate);
}
