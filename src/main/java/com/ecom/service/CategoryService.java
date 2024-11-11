package com.ecom.service;

import com.ecom.model.Category;

import java.util.List;

// Interface cung cấp các phương thức liên quan đến Category
public interface CategoryService {

    // Phương thức lưu danh mục
    public Category saveCategory(Category category);

    // Kiểm tra sự tồn tại của danh mục theo tên
    public Boolean existCategory(String name);

    // Lấy tất cả danh mục
    public List<Category> getAllCategory();

    // Xóa danh mục theo ID
    public Boolean deleteCategory(int id);

    // Lấy danh mục theo ID
    public Category getCategoryById(int id);

    public List<Category> getAllActiveCategory();
}
