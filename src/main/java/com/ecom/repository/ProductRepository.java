package com.ecom.repository;

import com.ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// Repository quản lý thực thể Product, kế thừa từ JpaRepository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Tìm tất cả sản phẩm đang hoạt động (isActive = true)
    List<Product> findByIsActiveTrue();

    // Tìm sản phẩm theo danh mục
    List<Product> findByCategory(String category);

    // Tìm sản phẩm theo tiêu đề hoặc danh mục, không phân biệt hoa thường
    List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2);

    // Tìm tất cả sản phẩm đang hoạt động và phân trang
    Page<Product> findByIsActiveTrue(Pageable pageable);

    // Tìm sản phẩm theo danh mục và phân trang
    Page<Product> findByCategory(Pageable pageable, String category);

    // Tìm sản phẩm theo tiêu đề hoặc danh mục và phân trang, không phân biệt hoa thường
    Page<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch1, Pageable pageable);
}
