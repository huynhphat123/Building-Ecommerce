package com.ecom.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

// Các annotation từ Lombok để tự động tạo các constructor, getter, setter
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity // Đánh dấu class này là một entity trong JPA
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng giá trị cho ID
    private int id;

    private String name; // Tên danh mục

    private String imageName; // Tên hình ảnh liên quan đến danh mục

    private Boolean isActive; // Trạng thái hoạt động của danh mục
}
