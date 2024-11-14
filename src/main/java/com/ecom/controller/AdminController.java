package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
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
import java.security.Principal;
import java.util.List;

// Controller xử lý các yêu cầu từ admin về danh mục sản phẩm
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService; // Inject CategoryService vào Controller

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void getUserDetails(Principal p, Model model) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user", userDtls);
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("categorys", allActiveCategory);
    }

    @GetMapping("/")
    public String index() {
        return "admin/index"; // Trả về view trang chủ admin
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model) {
        List<Category> categories = categoryService.getAllCategory();
        model.addAttribute("categories", categories);
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
    // Xử lý yêu cầu thêm sản phẩm
    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image, HttpSession session) throws IOException{
        // Kiểm tra nếu không có hình ảnh, sử dụng hình ảnh mặc định
        String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();

        // Gán tên hình ảnh vào sản phẩ
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());

        // Lưu sản phẩm vào cơ sở dữ liệu
        Product saveProduct = productService.saveProduct(product);

        if (!ObjectUtils.isEmpty(saveProduct)) {

            // Nếu sản phẩm lưu thành công, lưu hình ảnh vào thư mục
            File saveFile = new ClassPathResource("static/img").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
                    + image.getOriginalFilename());

            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Thông báo thành công
            session.setAttribute("succMsg", "Sản phẩm đã lưu thành công");
        } else {
            // Thông báo lỗi nếu không thành công
            session.setAttribute("errorMsg", "Sản phẩm lưu thất bại");
        }

        // Chuyển hướng đến trang thêm sản phẩm
        return "redirect:/admin/loadAddProduct";
    }

    // Xử lý yêu cầu xem danh sách sản phẩm
    @GetMapping("/products")
    public String loadViewProduct(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";  // quay lại trang sản phẩm
    }
    // Xử lý yêu cầu xóa sản phẩm
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        // Gọi dịch vụ để xóa sản phẩm
        Boolean deleteProduct = productService.deleteProduct(id);
        if (deleteProduct) {
            // Thông báo thành công khi xóa
            session.setAttribute("succMsg", "Xóa sản phẩm thành công");
        } else {
            // Thông báo lỗi nếu xóa không thành công
            session.setAttribute("errorMsg", "Xóa sản phẩm không thành công");
        }
        // Chuyển hướng về danh sách sản phẩm
        return "redirect:/admin/products";
    }

    // Xử lý yêu cầu chỉnh sửa sản phẩm
    @GetMapping("/editProduct/{id}")
    public String editProduct(@PathVariable int id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        model.addAttribute("categories", categoryService.getAllCategory());
        return "admin/edit_product";
    }

    // Xử lý yêu cầu cập nhật sản phẩm
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                                HttpSession session, Model model) {

            if(product.getDiscount() < 0 || product.getDiscount() > 100) {
                session.setAttribute("errorMsg", "Giảm giá không hợp lệ");
            } else {
                // Cập nhật sản phẩm với hình ảnh mới nếu có
                Product updateProduct = productService.updateProduct(product, image);
                if (!ObjectUtils.isEmpty(updateProduct)) {
                    session.setAttribute("succMsg", "Sản phẩm thay đổi thành công");
                } else {
                    session.setAttribute("errorMsg", "Sản phẩm thay đổi không thành công");
                }
            }
        // Chuyển hướng về trang chỉnh sửa sản phẩm
        return "redirect:/admin/editProduct/" + product.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<UserDtls> users = userService.getUsers("ROLE_USER");
        model.addAttribute("users", users);
        return "/admin/users";
    }
    @GetMapping("/updateStatus")
    public String updateUsAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
        Boolean f = userService.updateAccountStatus(id, status);
        if (f) {
            session.setAttribute("succMsg", "Trạng thái tài khoản đã được cập nhật");
        } else {
            session.setAttribute("errorMsg", "Có lỗi xảy ra ");
        }
        return "redirect:/admin/users";
    }
}
