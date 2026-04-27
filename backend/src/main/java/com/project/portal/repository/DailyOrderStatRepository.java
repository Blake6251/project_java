package com.project.portal.repository;

import com.project.portal.domain.DailyOrderStat;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyOrderStatRepository extends JpaRepository<DailyOrderStat, Long> {
    Optional<DailyOrderStat> findByStatDate(LocalDate statDate);
}
