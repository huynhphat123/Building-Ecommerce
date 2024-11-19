package com.ecom.service;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;

import java.util.List;


public interface OrderService {

    public void saveOrder(Integer userid, OrderRequest orderRequest);

    public List<ProductOrder> getOrdersByUser(Integer userId);

//    public ProductOrder updateOrderStatus(Integer id, String status);
//
//    public List<ProductOrder> getAllOrders();
//
//    public ProductOrder getOrdersByOrderId(String orderId);
//
//    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);
}
