package com.ecom.repository;

import com.ecom.model.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository quản lý thực thể ProductOrder, kế thừa từ JpaRepository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    // Tìm kiếm đơn hàng theo ID người dùng
    List<ProductOrder> findByUserId(Integer userId);

    // Tìm kiếm đơn hàng theo mã đơn hàng
    ProductOrder findByOrderId(String orderId);
}
