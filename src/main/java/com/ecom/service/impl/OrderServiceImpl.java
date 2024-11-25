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
        // Tìm các sản phẩm trong giỏ hàng của người dùng
        List<Cart> carts = cartRepository.findByUserId(userid);

        for (Cart cart : carts) {

            // Tạo đối tượng đơn hàng mới từ các thông tin trong giỏ hàng
            ProductOrder order = new ProductOrder();

            // Tạo ID cho đơn hàng và ngày đặt hàng
            order.setOrderId(UUID.randomUUID().toString());
            order.setOrderDate(LocalDate.now());

            // Thiết lập thông tin sản phẩm và giá của sản phẩm
            order.setProduct(cart.getProduct());
            order.setPrice(cart.getProduct().getDiscountPrice());

            // Thiết lập trạng thái ban đầu của đơn hàng
            order.setStatus(OrderStatus.N_PROGRESS.getName());
            order.setPaymentType(orderRequest.getPaymentType());

            // Thiết lập thông tin địa chỉ giao hàng
            OrderAddress address = new OrderAddress();
            address.setFirstName(orderRequest.getFirstName());
            address.setLastName(orderRequest.getLastName());
            address.setEmail(orderRequest.getEmail());
            address.setNumber(orderRequest.getNumber());
            address.setAddress(orderRequest.getAddress());
            address.setCity(orderRequest.getCity());
            address.setState(orderRequest.getState());
            address.setPincode(orderRequest.getPincode());

            order.setOrderAddress(address); // Gắn địa chỉ vào đơn hàng

            // Lưu đơn hàng vào cơ sở dữ liệu
            order.setOrderAddress(address);

            ProductOrder saveOrder = orderRepository.save(order);

            commonUtil.sendMailForProductOrder(saveOrder, "thành công");
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
        return orderRepository.findAll();
    }

    @Override
    public ProductOrder getOrdersByOrderId(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    @Override
    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return orderRepository.findAll(pageable);
    }


}
