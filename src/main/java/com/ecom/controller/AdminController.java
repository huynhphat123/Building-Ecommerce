package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.service.CategoryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

// Controller xử lý các yêu cầu từ admin về danh mục sản phẩm
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService; // Inject CategoryService vào Controller

    @GetMapping("/")
    public String index() {
        return "admin/index"; // Trả về view trang chủ admin
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct() {
        return "admin/add_product"; // Trả về view để thêm sản phẩm
    }

    @GetMapping("/category")
    public String category(Model model) {
        model.addAttribute("categorys", categoryService.getAllCategory()); // Lấy danh sách các danh mục và gửi vào model
        return "admin/category"; // Trả về view danh sách danh mục
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        String imageName = file != null ? file.getOriginalFilename() : "default.webp"; // Lấy tên file hình ảnh
        category.setImageName(imageName); // Gán tên hình ảnh cho danh mục

        Boolean existCategory = categoryService.existCategory(category.getName()); // Kiểm tra danh mục đã tồn tại chưa

        if (existCategory) {
            session.setAttribute("errorMsg", "Tên danh mục đã tồn tại"); // Thông báo lỗi nếu danh mục đã tồn tại
        } else {
            Category saveCategory = categoryService.saveCategory(category); // Lưu danh mục vào cơ sở dữ liệu
            if (ObjectUtils.isEmpty(saveCategory)) {
                session.setAttribute("errorMsg", "Không lưu được! Lỗi máy chủ nội bộ"); // Thông báo lỗi nếu không lưu được
            } else {
                // Lưu hình ảnh vào thư mục static/img/category_img
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING); // Lưu file hình ảnh

                session.setAttribute("succMsg", "Lưu danh mục thành công"); // Thông báo thành công
            }
        }
        return "redirect:/admin/category"; // Quay lại trang danh mục
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        Boolean deleteCategory = categoryService.deleteCategory(id); // Xóa danh mục theo ID
        if (deleteCategory) {
            session.setAttribute("succMsg", "Xóa danh mục thành công"); // Thông báo thành công
        } else {
            session.setAttribute("errorMsg", "Không tìm thấy danh mục để xóa"); // Thông báo lỗi nếu không tìm thấy danh mục
        }
        return "redirect:/admin/category"; // Quay lại trang danh mục
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id)); // Lấy thông tin danh mục để chỉnh sửa
        return "admin/edit_category"; // Trả về view để chỉnh sửa danh mục
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        Category oldCategory = categoryService.getCategoryById(category.getId()); // Lấy thông tin danh mục cũ
        String imageName = file.isEmpty() ? oldCategory.getImageName() : file.getOriginalFilename(); // Nếu không có hình ảnh mới thì giữ nguyên

        if (!ObjectUtils.isEmpty(category)) {
            oldCategory.setName(category.getName()); // Cập nhật tên danh mục
            oldCategory.setIsActive(category.getIsActive()); // Cập nhật trạng thái hoạt động
            oldCategory.setImageName(imageName); // Cập nhật tên hình ảnh
        }

        Category updateCategory = categoryService.saveCategory(oldCategory); // Cập nhật danh mục
        if (!ObjectUtils.isEmpty(updateCategory)) {
            // Lưu hình ảnh vào thư mục static/img/category_img
            if (!file.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

            session.setAttribute("succMsg", "Cập nhật danh mục thành công");
        } else {
            session.setAttribute("errorMsg", "Cập nhật danh mục thất bại");
        }

        return "redirect:/admin/category"; // Quay lại trang danh mục
    }
}
