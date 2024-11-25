package com.ecom.service;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import org.springframework.data.domain.Page;

import java.util.List;


public interface OrderService {

    // Lưu thông tin đơn hàng
    public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception;

    // Lấy danh sách đơn hàng của người dùng theo ID người dùng
    public List<ProductOrder> getOrdersByUser(Integer userId);

    // Cập nhật trạng thái đơn hàng
    public ProductOrder updateOrderStatus(Integer id, String status);

    // Lấy tất cả đơn hàng
   public List<ProductOrder> getAllOrders();

    // Lấy đơn hàng theo ID đơn hàng
     public ProductOrder getOrdersByOrderId(String orderId);

    // Lấy tất cả đơn hàng với phân trang
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);
}
