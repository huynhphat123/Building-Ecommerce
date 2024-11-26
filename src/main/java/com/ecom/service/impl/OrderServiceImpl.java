package com.ecom.service.impl;

import com.ecom.model.OrderAddress;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.Cart;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductOrderRepository;
import com.ecom.service.OrderService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {


    @Autowired
    private ProductOrderRepository orderRepository; // Repository để làm việc với đơn hàng

    @Autowired
    private CartRepository cartRepository; // Repository để làm việc với giỏ hàng

    @Autowired
    private CommonUtil commonUtil; // Util hỗ trợ các chức năng chung

    @Override
    public void saveOrder(Integer userid, OrderRequest orderRequest) throws Exception {
        // Lấy danh sách giỏ hàng của người dùng dựa trên ID người dùng
        List<Cart> carts = cartRepository.findByUserId(userid);

        // Lặp qua từng sản phẩm trong giỏ hàng
        for (Cart cart : carts) {

            // Tạo một đối tượng ProductOrder mới để lưu thông tin đơn hàng
            ProductOrder order = new ProductOrder();

            // Thiết lập ID cho đơn hàng bằng UUID ngẫu nhiên
            order.setOrderId(UUID.randomUUID().toString());

            // Lấy ngày hiện tại làm ngày đặt hàng
            order.setOrderDate(LocalDate.now());

            // Thiết lập sản phẩm và giá từ giỏ hàng
            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice()); // Lấy giá giảm của sản phẩm

            // Thiết lập số lượng sản phẩm từ giỏ hàng
            order.setQuantity(cart.getQuantity());

            // Gắn thông tin người dùng từ giỏ hàng vào đơn hàng
            order.setUser(cart.getUser());

            // Đặt trạng thái ban đầu của đơn hàng (IN_PROGRESS - đang xử lý)
            order.setStatus(OrderStatus.IN_PROGRESS.getName());

            // Gắn loại hình thanh toán từ yêu cầu đặt hàng
            order.setPaymentType(orderRequest.getPaymentType());

            // Thiết lập các thông tin về tên, email, số điện thoại và địa chỉ từ yêu cầu đặt hàng
            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setNumber(orderRequest.getNumber());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            // Gắn địa chỉ vào đối tượng đơn hàng
            order.setOrderAddress(address);

            // Lưu đơn hàng vào cơ sở dữ liệu
            ProductOrder saveOrder = orderRepository.save(order);

            // Gửi email thông báo đơn hàng đã được đặt thành công
            commonUtil.sendMailForProductOrder(saveOrder, "Thành công");
        }
    }

    @Override
    public List<ProductOrder> getOrdersByUser(Integer userId) {
        // Lấy tất cả các đơn hàng của người dùng dựa trên userId
        List<ProductOrder> orders = orderRepository.findByUserId(userId);
        return orders;
    }

    @Override
    public ProductOrder updateOrderStatus(Integer id, String status) {
        // Tìm đơn hàng theo ID
        Optional<ProductOrder> findById = orderRepository.findById(id);
        if (findById.isPresent()) {
            // Cập nhật trạng thái đơn hàng nếu tìm thấy
            ProductOrder productOrder = findById.get();
            productOrder.setStatus(status); // Cập nhật trạng thái
            // Lưu lại đơn hàng sau khi cập nhật
            ProductOrder updateOrder = orderRepository.save(productOrder);
            return updateOrder;
        }
        return null; // Nếu không tìm thấy đơn hàng, trả về null
    }

    @Override
    public List<ProductOrder> getAllOrders() {
        // Lấy tất cả đơn hàng
        return orderRepository.findAll();
    }

    @Override
    public ProductOrder getOrdersByOrderId(String orderId) {
        // Lấy đơn hàng theo mã đơn hàng
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        // Tạo đối tượng phân trang
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        // Lấy tất cả đơn hàng theo trang
        return orderRepository.findAll(pageable);
    }


}
