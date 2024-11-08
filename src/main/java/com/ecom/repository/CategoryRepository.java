package com.ecom.repository;

import com.ecom.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// Interface này kế thừa JpaRepository để cung cấp các phương thức cơ bản về CRUD cho Category
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    // Phương thức kiểm tra sự tồn tại của danh mục theo tên
    public Boolean existsByName(String name);
}
