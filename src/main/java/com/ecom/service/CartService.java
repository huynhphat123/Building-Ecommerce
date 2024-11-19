package com.ecom.service;

import com.ecom.model.Cart;
import org.springframework.stereotype.Service;

import java.util.List;

public interface CartService {

    // Lưu giỏ hàng và thêm sản phẩm vào giỏ hàng của người dùng
    public Cart saveCart(Integer productId, Integer userId);

    // Lấy tất cả giỏ hàng của người dùng
    public List<Cart> getCartsByUser(Integer userId);

    // Đếm số lượng giỏ hàng của người dùng
    public Integer getCountCart(Integer userId);

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    public void updateQuantity(String sy, Integer cid);
}


//Tổng kết:
//Thêm sản phẩm vào giỏ hàng: Chức năng cho phép người dùng thêm sản phẩm vào giỏ hàng. Nếu sản phẩm đã có trong giỏ hàng, sẽ tăng số lượng lên 1, còn nếu chưa có sẽ tạo mới một mục giỏ hàng.
//Xem giỏ hàng: Người dùng có thể xem các sản phẩm trong giỏ hàng, cùng với tổng giá trị đơn hàng.
//Cập nhật số lượng sản phẩm: Người dùng có thể tăng hoặc giảm số lượng sản phẩm trong giỏ hàng, và giỏ hàng sẽ tự động được cập nhật.