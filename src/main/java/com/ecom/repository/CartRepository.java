package com.ecom.repository;

import com.ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    // Tìm giỏ hàng của người dùng theo ID sản phẩm và ID người dùng
    public Cart findByProductIdAndUserId(Integer productId, Integer userId);

    // Đếm số lượng giỏ hàng của người dùng
    public Integer countByUserId(Integer userId);

    // Tìm tất cả giỏ hàng của người dùng
    public List<Cart> findByUserId(Integer userId);
}
