package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

// Các annotation của Lombok để tự động sinh constructor, getter và setter cho class này
@AllArgsConstructor   // Tự động sinh constructor có đầy đủ tham số
@NoArgsConstructor    // Tự động sinh constructor không tham số
@Getter               // Tự động sinh getter cho các thuộc tính
@Setter               // Tự động sinh setter cho các thuộc tính

@Entity               // Đánh dấu class này là một entity, map với bảng trong cơ sở dữ liệu
public class UserDtls implements Serializable {
    private static final long serialVersionUID = 1L; // Đảm bảo serialVersionUID có thể giúp kiểm tra sự tương thích giữa các phiên bản của lớp

    @Id  // Đánh dấu thuộc tính `id` là khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Tự động sinh giá trị cho trường `id` theo chiến lược auto increment
    private Integer id;

    private String name;          // Tên người dùng

    private String number;        // Số điện thoại

    private String email;         // Email của người dùng

    private String address;       // Địa chỉ

    private String city;          // Thành phố

    private String password;      // Mật khẩu của người dùng

    private String profileImage;  // Tên file ảnh đại diện của người dùng

    private String role;

    private Boolean isEnable;           // Trạng thái hoạt động của tài khoản

    private Boolean accountNonLocked;   // Trạng thái khóa tài khoản (true nếu chưa bị khóa)

    private Integer faildAttempts;      // Số lần nhập mật khẩu sai

    private Date lockTime;              // Thời gian bắt đầu khóa tài khoản

    private String resetToken;



}
