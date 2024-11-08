package com.ecom.service;

import com.ecom.model.Category;
import com.ecom.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;

// Đánh dấu lớp này là Service trong Spring, để Spring quản lý và cung cấp đối tượng của lớp này khi cần thiết.
@Service
public class CategoryServiceImpl implements CategoryService {

    // Inject CategoryRepository vào service để có thể tương tác với cơ sở dữ liệu thông qua repository
    @Autowired
    private CategoryRepository categoryRepository;

    // Phương thức lưu danh mục vào cơ sở dữ liệu
    @Override
    public Category saveCategory(Category category) {
        // Lưu đối tượng category vào cơ sở dữ liệu và trả về đối tượng đã được lưu
        return categoryRepository.save(category);
    }

    // Phương thức lấy tất cả danh mục từ cơ sở dữ liệu
    @Override
    public List<Category> getAllCategory() {
        // Sử dụng categoryRepository để lấy tất cả danh mục từ cơ sở dữ liệu và trả về danh sách các danh mục
        return categoryRepository.findAll();
    }

    // Phương thức kiểm tra xem danh mục có tồn tại trong cơ sở dữ liệu hay không
    @Override
    public Boolean existCategory(String name) {
        // Kiểm tra danh mục có tồn tại bằng cách tìm kiếm theo tên, trả về true nếu tồn tại, false nếu không
        return categoryRepository.existsByName(name);
    }

    // Phương thức xóa danh mục khỏi cơ sở dữ liệu theo ID
    @Override
    public Boolean deleteCategory(int id) {
        // Tìm danh mục theo ID từ cơ sở dữ liệu
        Category category = categoryRepository.findById(id).orElse(null);

        if(category != null) {
            // Nếu danh mục tồn tại, tiến hành xóa khỏi cơ sở dữ liệu
            categoryRepository.delete(category);
            // Trả về true khi xóa thành công
            return true;
        }

        // Nếu không tìm thấy danh mục, trả về false
        return false;
    }

    // Phương thức lấy một danh mục cụ thể theo ID
    @Override
    public Category getCategoryById(int id) {
        // Tìm danh mục theo ID từ cơ sở dữ liệu và trả về đối tượng danh mục tìm thấy, nếu không trả về null
        Category category = categoryRepository.findById(id).orElse(null);
        return category;
    }
}
