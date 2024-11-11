package com.ecom.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity  // Đánh dấu lớp này là một thực thể JPA, tương ứng với bảng trong cơ sở dữ liệu
@AllArgsConstructor  // Tạo constructor với tất cả các tham số cho tất cả các trường trong lớp
@NoArgsConstructor  // Tạo constructor mặc định không có tham số
@Getter  // Tạo phương thức getter cho tất cả các trường trong lớp
@Setter  // Tạo phương thức setter cho tất cả các trường trong lớp
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 500)
    private String title;

    @Column(length = 5000)
    private String description;

    private String category;

    private Double price;

    private int stock;

    private String image;

    private int discount;

    private Double discountPrice;

    private Boolean isActive;



}
