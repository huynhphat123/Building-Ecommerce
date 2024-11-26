package com.ecom.util;

public enum OrderStatus {

    IN_PROGRESS(1, "Đang xử lý"),
    ORDER_RECEIVED(2, "Đã nhận đơn"),
    PRODUCT_PACKED(3, "Đã đóng gói"),
    OUT_FOR_DELIVERY(4, "Đang giao hàng"),
    DELIVERED(5, "Đã giao hàng"),
    CANCEL(6, "Đã hủy"),
    SUCCESS(7, "Thành công");

    private Integer id;

    private String name;

    private OrderStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
