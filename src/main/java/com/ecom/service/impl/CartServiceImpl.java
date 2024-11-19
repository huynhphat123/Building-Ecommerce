package com.ecom.service.impl;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.repository.UserReponsitory;
import com.ecom.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;  // Kho lưu trữ giỏ hàng

    @Autowired
    private UserReponsitory userReponsitory;  // Kho lưu trữ người dùng

    @Autowired
    private ProductRepository productRepository;  // Kho lưu trữ sản phẩm

    // Lưu giỏ hàng và thêm sản phẩm vào giỏ hàng của người dùng
    @Override
    public Cart saveCart(Integer productId, Integer userId) {
        // Lấy thông tin người dùng và sản phẩm từ cơ sở dữ liệu
        UserDtls userDtls = userReponsitory.findById(userId).get();
        Product product = productRepository.findById(productId).get();

        // Kiểm tra xem sản phẩm đã có trong giỏ hàng của người dùng chưa
        Cart cartStatus = cartRepository.findByProductIdAndUserId(productId, userId);

        Cart cart = null;

        // Nếu sản phẩm chưa có trong giỏ hàng, tạo mới giỏ hàng và thêm sản phẩm vào
        if (ObjectUtils.isEmpty(cartStatus)) {
            cart = new Cart();
            cart.setProduct(product);
            cart.setUser(userDtls);
            cart.setQuantity(1);  // Mặc định số lượng là 1 khi thêm mới sản phẩm vào giỏ hàng
            cart.setTotalPrice(1 * product.getDiscountPrice());  // Tính tổng giá trị sản phẩm
        } else {
            // Nếu sản phẩm đã có trong giỏ hàng, cập nhật số lượng và tính lại tổng giá trị
            cart = cartStatus;
            cart.setQuantity(cart.getQuantity() + 1);  // Tăng số lượng sản phẩm
            cart.setTotalPrice(cart.getQuantity() * cart.getProduct().getDiscountPrice());  // Tính lại giá trị tổng
        }

        // Lưu giỏ hàng vào cơ sở dữ liệu và trả về giỏ hàng đã lưu
        Cart saveCart = cartRepository.save(cart);
        return saveCart;
    }

    // Lấy tất cả giỏ hàng của người dùng và tính tổng giá trị đơn hàng
    @Override
    public List<Cart> getCartsByUser(Integer userId) {
        // Lấy danh sách giỏ hàng của người dùng
        List<Cart> carts = cartRepository.findByUserId(userId);

        // Khởi tạo biến tổng giá trị đơn hàng
        Double totalOrderPrice = 0.0;
        List<Cart> updateCarts = new ArrayList<>();

        // Duyệt qua từng giỏ hàng để tính tổng giá trị cho mỗi sản phẩm và tổng đơn hàng
        for (Cart c : carts) {
            Double totalPrice = (c.getProduct().getDiscountPrice() * c.getQuantity());  // Tính tổng giá trị sản phẩm
            c.setTotalPrice(totalPrice);
            totalOrderPrice = totalOrderPrice + totalPrice;  // Cộng dồn giá trị vào tổng đơn hàng
            c.setTotalOrderPrice(totalOrderPrice);  // Cập nhật tổng giá trị đơn hàng cho giỏ hàng
            updateCarts.add(c);  // Thêm giỏ hàng đã cập nhật vào danh sách
        }
        return updateCarts;  // Trả về danh sách giỏ hàng đã cập nhật
    }

    // Đếm số lượng giỏ hàng của người dùng
    @Override
    public Integer getCountCart(Integer userId) {
        Integer countByUserId = cartRepository.countByUserId(userId);  // Đếm số lượng giỏ hàng
        return countByUserId;  // Trả về số lượng giỏ hàng của người dùng
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @Override
    public void updateQuantity(String sy, Integer cid) {
        Cart cart = cartRepository.findById(cid).get();  // Lấy giỏ hàng theo ID

        int updateQuantity;

        if (sy.equalsIgnoreCase("de")) {
            // Nếu giảm số lượng sản phẩm
            updateQuantity = cart.getQuantity() - 1;

            // Nếu số lượng sản phẩm giảm xuống 0 hoặc thấp hơn, xóa giỏ hàng
            if (updateQuantity <= 0) {
                cartRepository.delete(cart);
            } else {
                // Cập nhật lại số lượng sản phẩm và lưu giỏ hàng
                cart.setQuantity(updateQuantity);
                cartRepository.save(cart);
            }

        } else {
            // Nếu tăng số lượng sản phẩm
            updateQuantity = cart.getQuantity() + 1;
            cart.setQuantity(updateQuantity);
            cartRepository.save(cart);  // Lưu giỏ hàng đã cập nhật
        }
    }
}

