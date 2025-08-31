package com.sportshop.sportshop.repository;

import java.time.LocalDate;
import java.util.List;

import com.sportshop.sportshop.dto.response.OrderItemRowResponse;
import com.sportshop.sportshop.enums.StatusOrderEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sportshop.sportshop.entity.OrderDetailEntity;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetailEntity, Long> {
    List<OrderDetailEntity> findByOrderId(Long orderId);

    List<OrderDetailEntity> findByProductId(Long productId);
    @Query(value = """
        SELECT p.name AS product_name, COALESCE(SUM(od.total),0) AS revenue
        FROM order_detail od
        JOIN orders  o ON od.order_id   = o.id
        JOIN product p ON od.product_id = p.id
        WHERE (:from IS NULL OR o.`date` >= :from)
          AND (:to   IS NULL OR o.`date` < DATE_ADD(:to, INTERVAL 1 DAY))
          AND o.status_order = 'Da_Giao'
        GROUP BY p.name
        ORDER BY revenue DESC
        LIMIT 15
    """, nativeQuery = true)
    List<Object[]> revenueByProduct(@Param("from") LocalDate from, @Param("to") LocalDate to);

    @Query(value = """
        SELECT cp.name_category AS category_name,
               COALESCE(SUM(od.total - IFNULL(od.discount,0)), 0) AS revenue
        FROM orders o
        JOIN order_detail od  ON od.order_id  = o.id
        JOIN product p        ON p.id         = od.product_id         -- nếu bảng là 'products' thì đổi tên bảng
        JOIN category_product cp ON cp.id     = p.category_id         -- ⬅ nếu cột thật là 'category_product_id' thì đổi thành:  p.category_product_id
        WHERE (:from IS NULL OR o.`date` >= :from)
          AND (:to   IS NULL OR o.`date` < DATE_ADD(:to, INTERVAL 1 DAY))
          AND o.status_order = 'Da_Giao'
        GROUP BY cp.id, cp.name_category
        HAVING revenue > 0
        ORDER BY revenue DESC
    """, nativeQuery = true)
    List<Object[]> revenueByCategory(
            @Param("from") LocalDate from,
            @Param("to")   LocalDate to
    );
    @Query("""
        select new com.sportshop.sportshop.dto.response.OrderItemRowResponse(
            o.id, o.date, p.name, od.quantity, od.price, od.total, o.status
        )
        from OrderEntity o
        left join o.items od
         left join od.product p
        where o.user.id = :userId
          and o.status   = :status
        order by o.date desc, o.id desc, od.id asc
    """)
    List<OrderItemRowResponse> findUserOrderItemsByStatusDto(@Param("userId") Long userId,
                                                             @Param("status") StatusOrderEnum status);
}
