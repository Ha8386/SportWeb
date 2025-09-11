package com.sportshop.sportshop.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sportshop.sportshop.entity.OrderEntity;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByUserId(Long userId);
    Optional<OrderEntity> findByIdAndUserId(Long id, Long userId);

    @Query(value = "SELECT DATE(o.date) AS date, SUM(o.total) AS revenue " +
               "FROM orders o WHERE o.date BETWEEN :startDate AND :endDate " +
               "GROUP BY DATE(o.date) " +
               "ORDER BY DATE(o.date) ASC", nativeQuery = true)
    List<Object[]> findRevenueByDay(@Param("startDate") LocalDateTime startDate,
                                @Param("endDate") LocalDateTime endDate);
    @Query(value = """
        SELECT COALESCE(SUM(o.total),0)
        FROM orders o
        WHERE (:from IS NULL OR o.`date` >= :from)
          AND (:to   IS NULL OR o.`date` < DATE_ADD(:to, INTERVAL 1 DAY))
          AND o.status_order = 'Da_Giao'
    """, nativeQuery = true)
    Double sumRevenue(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // Doanh thu theo NGÀY
    @Query(value = """
        SELECT DATE(o.`date`) AS d, COALESCE(SUM(o.total),0) AS s
        FROM orders o
        WHERE (:from IS NULL OR o.`date` >= :from)
          AND (:to   IS NULL OR o.`date` < DATE_ADD(:to, INTERVAL 1 DAY))
          AND o.status_order = 'Da_Giao'
        GROUP BY DATE(o.`date`)
        ORDER BY d ASC
    """, nativeQuery = true)
    List<Object[]> revenueByDay(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // Doanh thu theo THÁNG (YYYY-MM)
    @Query(value = """
        SELECT DATE_FORMAT(o.`date`,'%Y-%m') AS ym, COALESCE(SUM(o.total),0) AS s
        FROM orders o
        WHERE (:from IS NULL OR o.`date` >= :from)
          AND (:to   IS NULL OR o.`date` < DATE_ADD(:to, INTERVAL 1 DAY))
          AND o.status_order = 'Da_Giao'
        GROUP BY DATE_FORMAT(o.`date`,'%Y-%m')
        ORDER BY ym ASC
    """, nativeQuery = true)
    List<Object[]> revenueByMonth(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // Doanh thu theo NĂM
    @Query(value = """
        SELECT YEAR(o.`date`) AS y, COALESCE(SUM(o.total),0) AS s
        FROM orders o
        WHERE (:from IS NULL OR o.`date` >= :from)
          AND (:to   IS NULL OR o.`date` < DATE_ADD(:to, INTERVAL 1 DAY))
          AND o.status_order = 'Da_Giao'
        GROUP BY YEAR(o.`date`)
        ORDER BY y ASC
    """, nativeQuery = true)
    List<Object[]> revenueByYear(@Param("from") LocalDate from, @Param("to") LocalDate to);

    // Số lượng đơn (loại đơn hủy)
    @Query(value = """
        SELECT COUNT(*)
        FROM orders o
        WHERE (:from IS NULL OR o.`date` >= :from)
          AND (:to   IS NULL OR o.`date` < DATE_ADD(:to, INTERVAL 1 DAY))
          AND o.status_order <> 'Da_Huy'
    """, nativeQuery = true)
    Long countOrdersInRange(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query("""
  select distinct o
  from OrderEntity o
  left join fetch o.items i
  left join fetch i.product p
  where o.user.id = :userId
  order by o.date desc
""")
    List<OrderEntity> findAllWithItemsByUserId(@Param("userId") Long userId);

    @Query("""
        select distinct o
        from OrderEntity o
        left join fetch o.items i
        left join fetch i.product p
        where o.id = :id
    """)
    Optional<OrderEntity> findByIdFetchItems(@Param("id") Long id);
}
