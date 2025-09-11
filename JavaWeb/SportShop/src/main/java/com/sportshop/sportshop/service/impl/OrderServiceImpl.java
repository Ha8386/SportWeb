package com.sportshop.sportshop.service.impl;

import java.util.Date;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.sportshop.sportshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sportshop.sportshop.dto.response.DailyRevenue;
import com.sportshop.sportshop.dto.response.OrderResponse;
import com.sportshop.sportshop.entity.CartEntity;
import com.sportshop.sportshop.entity.OrderDetailEntity;
import com.sportshop.sportshop.entity.OrderEntity;
import com.sportshop.sportshop.entity.ProductEntity;
import com.sportshop.sportshop.entity.UserEntity;
import com.sportshop.sportshop.enums.StatusOrderEnum;
import com.sportshop.sportshop.mapper.OrderMapper;
import com.sportshop.sportshop.repository.OrderDetailRepository;
import com.sportshop.sportshop.repository.OrderRepository;
import com.sportshop.sportshop.service.CartService;
import com.sportshop.sportshop.service.OrderService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private OrderDetailRepository orderDetailRepository;
    @Autowired private OrderMapper orderMapper;
    @Autowired private ProductRepository productRepository;

    // Lấy giỏ để đổ vào Order khi tạo
    @Autowired private CartService cartService;

    @Override
    public Long getCount(){
        return orderRepository.count();
    }

    @Override
    public Long getIncrease(){
        return orderRepository.findAll()
                .stream()
                .mapToLong(order -> order.getTotal() != null ? order.getTotal() : 0L)
                .sum();
    }

    @Override
    public List<OrderResponse> getAllOrder() {
        List<OrderResponse> result = new ArrayList<>();
        for (OrderEntity order : orderRepository.findAll()) {
            if (order.getTotal() == null) {
                order.setTotal(0L);
                orderRepository.save(order);
            }
            result.add(orderMapper.toOrderResponse(order));
        }
        return result;
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return null;
        if (order.getTotal() == null) {
            order.setTotal(0L);
            orderRepository.save(order);
        }
        return orderMapper.toOrderResponse(order);
    }

    /**
     * Tạo order TỪ GIỎ của user:
     *  - Tạo Order (status = Dang_Xu_Ly)
     *  - Đổ OrderDetail từ Cart
     *  - Tính total theo đơn giá sau giảm * quantity
     *  - SET quantity = TỔNG SỐ LƯỢNG (SUM(cart.quantity))
     */
    @Override
    public Long AddOrder(UserEntity user) {
        // Lấy giỏ của user
        List<CartEntity> carts = cartService.getCart(user.getId());

        // Tạo order
        OrderEntity order = new OrderEntity();
        order.setDate(new Date());
        order.setUser(user);
        order.setStatus(StatusOrderEnum.Dang_Xu_Ly); // chờ thanh toán/xử lý
        order.setTotal(0L);
        order.setQuantity(0L);
        orderRepository.save(order); // cần ID trước để gắn cho detail

        long sumQuantity = 0L;
        long sumTotal = 0L;

        if (carts != null) {
            for (CartEntity cart : carts) {
                ProductEntity p = cart.getProduct();
                long qty = (cart.getQuantity() == null) ? 0L : cart.getQuantity();
                if (p == null || qty <= 0) continue;

                // Ép price (Long) -> BigDecimal để tính discount
                Long priceLong = (p.getPrice() == null) ? 0L : p.getPrice();
                BigDecimal priceBD = BigDecimal.valueOf(priceLong);

                long discount = (p.getDiscount() == null) ? 0 : p.getDiscount(); // % giảm
                BigDecimal unitAfterDiscount = priceBD
                        .multiply(BigDecimal.valueOf(100 - discount))
                        .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP); // làm tròn 0 chữ số

                // lineTotal = đơn giá sau giảm * qty
                long lineTotal = unitAfterDiscount.longValue() * qty;

                // Tạo OrderDetail từ cart
                OrderDetailEntity detail = new OrderDetailEntity();
                detail.setOrder(order);
                detail.setProduct(p);
                // setQuantity nhận Long -> truyền long (tự auto-box)
                detail.setQuantity(qty);
                // Nếu setPrice nhận Long -> truyền long (auto-box). Nếu là Integer, đổi sang intValue().
                detail.setPrice(unitAfterDiscount.longValue());
                detail.setTotal(lineTotal);
                orderDetailRepository.save(detail);

                sumQuantity += qty;
                sumTotal += lineTotal;
            }
        }

        // Cập nhật lại đơn
        order.setQuantity(sumQuantity);   // tổng số lượng (quantity_product)
        order.setTotal(sumTotal);         // tổng tiền sau giảm
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * Tính lại total & quantity từ OrderDetails:
     *  - total = SUM(item.total)
     *  - quantity = SUM(item.quantity)
     */
    @Override
    public void UpdateToTalPrice(Long orderId) {
        long totalPrice = 0L;
        long totalQty = 0L;

        List<OrderDetailEntity> orderDetails = orderDetailRepository.findByOrderId(orderId);
        for (OrderDetailEntity item : orderDetails) {
            Long lineTotal = item.getTotal();     // Long
            Long lineQty   = item.getQuantity();  // Long
            totalPrice += (lineTotal != null ? lineTotal : 0L);
            totalQty   += (lineQty   != null ? lineQty   : 0L);
        }

        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return;

        order.setTotal(totalPrice);   // Long <- long (auto-box)
        order.setQuantity(totalQty);  // Long <- long (auto-box)
        orderRepository.save(order);
    }

    @Override
    public List<OrderResponse> historyBuy(Long userId) {
        List<OrderResponse> result = new ArrayList<>();
        for (OrderEntity order : orderRepository.findByUserId(userId)) {
            result.add(orderMapper.toOrderResponse(order));
        }
        return result;
    }

    @Override
    public void updateStatusOrder(Long orderId, StatusOrderEnum status){
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) return;
        order.setStatus(status);
        orderRepository.save(order);
    }

    @Override
    public List<DailyRevenue> getRevenueByDay(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Object[]> rawData = orderRepository.findRevenueByDay(startDateTime, endDateTime);
        List<DailyRevenue> dailyRevenues = new ArrayList<>();

        for (Object[] row : rawData) {
            String date = ((java.util.Date) row[0]).toString();
            BigDecimal revenue = (BigDecimal) row[1];
            dailyRevenues.add(new DailyRevenue(date, revenue));
        }

        return dailyRevenues;
    }

    @Transactional
    @Override
    public void confirmPaidAndAdjustStock(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        // stockAdjusted đang @Transient => sẽ reset khi app restart.
        // Khuyến nghị đổi sang @Column(name="stock_adjusted") để lưu DB (an toàn chống trừ lặp).
        if (Boolean.TRUE.equals(order.getStockAdjusted())) {
            return; // đã trừ kho (trong vòng đời app)
        }

        var items = order.getItems();
        if (items == null || items.isEmpty()) {
            // Không có dòng hàng để trừ kho
            return;
        }

        for (OrderDetailEntity d : items) {
            var product = productRepository.lockById(d.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + d.getProduct().getId()));

            long qty = (d.getQuantity() == null ? 0L : d.getQuantity());
            long current = (product.getQuantity() == null ? 0L : product.getQuantity());
            long sold    = (product.getQuantitySell() == null ? 0L : product.getQuantitySell());

            if (qty <= 0) continue;

            if (current < qty) {
                throw new IllegalStateException("Not enough stock for product id=" + product.getId());
            }

            product.setQuantity(current - qty);
            product.setQuantitySell(sold + qty);
            productRepository.save(product);
        }

        // đánh dấu đã trừ kho (runtime). Khuyên dùng cột DB để bền vững.
        order.setStockAdjusted(true);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void confirmCancel(Long orderId) {
        // Nên dùng fetch join / @EntityGraph để lấy kèm items và product
        OrderEntity order = orderRepository.findByIdFetchItems(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));

        if (order.getStatus() != StatusOrderEnum.Yeu_Cau_Huy) {
            throw new IllegalStateException("Đơn không ở trạng thái yêu cầu hủy");
        }

        for (OrderDetailEntity item : order.getItems()) {
            ProductEntity p = item.getProduct();
            long q = item.getQuantity(); // quantity_product trong bảng orders

            p.setQuantity(p.getQuantity() + q);
            long sold = p.getQuantitySell() == null ? 0 : p.getQuantitySell();
            sold = Math.max(sold - q, 0);
            p.setQuantitySell(sold);

            productRepository.save(p);
        }

        order.setStatus(StatusOrderEnum.Da_Huy);
        orderRepository.save(order);
    }

    @Transactional
    @Override
    public void rejectCancel(Long orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng"));
        if (order.getStatus() != StatusOrderEnum.Yeu_Cau_Huy) {
            throw new IllegalStateException("Đơn không ở trạng thái yêu cầu hủy");
        }
        order.setStatus(StatusOrderEnum.Dang_Xu_Ly);
        orderRepository.save(order);
    }
}
