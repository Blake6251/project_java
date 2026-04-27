package com.project.portal.repository;

import com.project.portal.domain.Order;
import com.project.portal.domain.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, Long>, QuerydslOrderRepository {

    @Query("SELECT o FROM Order o JOIN FETCH o.user WHERE o.id = :id")
    Optional<Order> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.user")
    List<Order> findAllWithUser();

    @Query("""
            SELECT function('date', o.createdAt), COUNT(o), COALESCE(SUM(o.quantity), 0)
            FROM Order o
            GROUP BY function('date', o.createdAt)
            ORDER BY function('date', o.createdAt) DESC
            """)
    List<Object[]> findDailyStats();

    @Query("""
            SELECT o.status, COUNT(o)
            FROM Order o
            GROUP BY o.status
            """)
    List<Object[]> countByStatus();

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime createdAt);

    long countByStatus(OrderStatus status);

    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    Page<Order> findByUser_UsernameOrderByCreatedAtDesc(String username, Pageable pageable);
}
