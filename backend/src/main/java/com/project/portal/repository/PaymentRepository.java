package com.project.kiosk.repository;

import com.project.kiosk.domain.Payment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p JOIN FETCH p.order o JOIN FETCH o.user WHERE p.id = :id")
    Optional<Payment> findByIdWithOrderAndUser(@Param("id") Long id);
}
