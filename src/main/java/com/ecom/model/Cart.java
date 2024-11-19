package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // ID giỏ hàng, được tự động sinh ra

    @ManyToOne
    private UserDtls user;  // Quan hệ với người dùng sở hữu giỏ hàng

    @ManyToOne
    private Product product;  // Quan hệ với sản phẩm trong giỏ hàng

    private Integer quantity;  // Số lượng sản phẩm trong giỏ hàng

    @Transient
    private Double totalPrice;  // Tổng giá trị của sản phẩm trong giỏ hàng (chưa tính tổng đơn hàng)

    @Transient
    private Double totalOrderPrice;  // Tổng giá trị của đơn hàng (tính tổng cho tất cả sản phẩm trong giỏ hàng)
}

