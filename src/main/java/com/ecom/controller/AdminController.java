package com.ecom.controller;

import com.ecom.model.*;
import com.ecom.service.*;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
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

    // Inject các dịch vụ liên quan đến Category, Product, User và Cart
    @Autowired
    private CategoryService categoryService; // Dịch vụ quản lý danh mục sản phẩm

    @Autowired
    private ProductService productService; // Dịch vụ quản lý sản phẩm

    @Autowired
    private UserService userService; // Dịch vụ quản lý người dùng

    @Autowired
    private CartService cartService; // Dịch vụ quản lý giỏ hàng

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;  // Các hàm tiện ích chung
    @ModelAttribute
    public void getUserDetails(Principal p, Model model) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            model.addAttribute("countCart", countCart); // Hiển thị số lượng giỏ hàng
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("categorys", allActiveCategory); // Hiển thị danh mục sản phẩm
    }

    // Hiển thị trang chủ của admin
    @GetMapping("/")
    public String index() {
        return "admin/index"; // Trả về view trang chủ admin
    }

    // Tải trang thêm sản phẩm
    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model model) {
        List<Category> categories = categoryService.getAllCategory(); // Lấy tất cả danh mục để hiển thị
        model.addAttribute("categories", categories); // Truyền danh sách danh mục vào model
        return "admin/add_product"; // Trả về view để thêm sản phẩm
    }

    @GetMapping("/category")
    public String category(Model model, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name = "pageSize", defaultValue = "8") Integer pageSize) {

        Page<Category> page = categoryService.getAllCategorPagination(pageNo, pageSize);
        List<Category> categorys = page.getContent();
        model.addAttribute("categorys", categorys);

        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        return "admin/category";  // Trả về view danh sách danh mục
    }

    // Xử lý lưu danh mục mới
    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
        String imageName = file != null ? file.getOriginalFilename() : "default.webp"; // Kiểm tra và lấy tên file hình ảnh
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
        product.setImage(imageName); // Gán tên hình ảnh cho sản phẩm
        product.setDiscount(0); // Mặc định không có giảm giá
        product.setDiscountPrice(product.getPrice()); // Đặt giá giảm giá bằng giá gốc

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
    public String loadViewProduct(Model model,@RequestParam(defaultValue = "") String ch,
                                  @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                  @RequestParam(name = "pageSize", defaultValue = "8") Integer pageSize) {

        Page<Product> page = null;

        // Nếu chuỗi tìm kiếm 'ch' không rỗng, thực hiện tìm kiếm phân trang
        if (ch != null && ch.length() > 0) {
            page = productService.searchProductPagination(pageNo, pageSize, ch);
        } else {
            // Nếu không có từ khóa tìm kiếm, lấy tất cả sản phẩm và phân trang
            page = productService.getAllProductsPagination(pageNo, pageSize);
        }
        // Thêm danh sách sản phẩm vào model để hiển thị
        model.addAttribute("products", page.getContent());

        // Thêm các thông tin về phân trang vào model
        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        return "admin/products"; // Trả về trang admin/product.html
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
        model.addAttribute("product", productService.getProductById(id)); // Lấy sản phẩm theo ID
        model.addAttribute("categories", categoryService.getAllCategory()); // Lấy tất cả danh mục để hiển thị
        return "admin/edit_product"; // Trả về view để chỉnh sửa sản phẩm
    }

    // Xử lý yêu cầu cập nhật sản phẩm
    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
                                HttpSession session, Model model) {

            if(product.getDiscount() < 0 || product.getDiscount() > 100) {
                session.setAttribute("errorMsg", "Giảm giá không hợp lệ"); // Thông báo lỗi nếu giá giảm không hợp lệ
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
        // Lấy danh sách tất cả người dùng có vai trò là "ROLE_USER"
        List<UserDtls> users = userService.getUsers("ROLE_USER");

        // Thêm danh sách người dùng vào model để truyền dữ liệu vào view
        model.addAttribute("users", users);

        // Trả về trang quản lý người dùng trong admin
        return "/admin/users";
    }
    @GetMapping("/updateStatus")
    public String updateUsAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, HttpSession session) {
        // Cập nhật trạng thái tài khoản của người dùng dựa trên id và trạng thái được truyền vào
        Boolean f = userService.updateAccountStatus(id, status);

        // Nếu cập nhật thành công
        if (f) {
            session.setAttribute("succMsg", "Trạng thái tài khoản đã được cập nhật");
        } else {
            session.setAttribute("errorMsg", "Có lỗi xảy ra ");
        }

        // Chuyển hướng lại về trang danh sách người dùng
        return "redirect:/admin/users";
    }

    // Lấy danh sách tất cả đơn hàng có phân trang
    @GetMapping("/orders")
    public String getAllOrders(Model model, @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "1") Integer pageSize) {

        // Gọi service để lấy danh sách đơn hàng với phân trang
        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);

        // Thêm danh sách đơn hàng vào model
        model.addAttribute("orders", page.getContent());
        model.addAttribute("srch", false);

        // Thêm các thông tin về phân trang
        model.addAttribute("pageNo", page.getNumber());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("isFirst", page.isFirst());
        model.addAttribute("isLast", page.isLast());

        return "/admin/orders"; // Trả về trang admin/orders.html
    }

    // Cập nhật trạng thái đơn hàng
    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) {

        // Lấy danh sách trạng thái đơn hàng từ enum
        OrderStatus[] values = OrderStatus.values();
        String status = null;

        // Tìm trạng thái tương ứng với ID truyền vào
        for (OrderStatus orderSt : values) {
            if (orderSt.getId().equals(st)) {
                status = orderSt.getName();
            }
        }

        // Gọi service để cập nhật trạng thái đơn hàng
        ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

        try {
            // Gửi email thông báo về việc cập nhật trạng thái đơn hàng
            commonUtil.sendMailForProductOrder(updateOrder, status);
        } catch (Exception e) {
            e.printStackTrace(); // Ghi log lỗi nếu không thể gửi email
        }

        // Kiểm tra nếu việc cập nhật thành công
        if (!ObjectUtils.isEmpty(updateOrder)) {
            session.setAttribute("succMsg", "Trạng thái đã cập nhật");
        } else {
            session.setAttribute("errorMsg", "Trạng thái chưa được cập nhật");
        }
        // Điều hướng quay lại trang danh sách đơn hàng
        return "redirect:/admin/orders";
    }

    // Tìm kiếm đơn hàng theo mã đơn hàng
    @GetMapping("/search-order")
    public String searchProduct(@RequestParam String orderId, Model model, HttpSession session,@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "2") Integer pageSize) {

        // Kiểm tra nếu có mã đơn hàng được nhập
        if (orderId != null && orderId.length() > 0) {

            // Gọi service để tìm kiếm đơn hàng theo mã đơn hàng
            ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());

            // Kiểm tra nếu đơn hàng không tồn tại
            if (ObjectUtils.isEmpty(order)) {
                session.setAttribute("errorMsg", "Mã đơn hàng không chính xác");
                model.addAttribute("orderDtls", null);
            } else {
                // Thêm chi tiết đơn hàng vào model
                model.addAttribute("orderDtls", order);
            }
            model.addAttribute("srch", true); // Đặt cờ tìm kiếm thành true
        } else {
            // Nếu không có mã đơn hàng, hiển thị tất cả đơn hàng
            Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
            model.addAttribute("orders", page);
            model.addAttribute("srch", false);

            model.addAttribute("pageNo", page.getNumber());
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("totalElements", page.getTotalElements());
            model.addAttribute("totalPages", page.getTotalPages());
            model.addAttribute("isFirst", page.isFirst());
            model.addAttribute("isLast", page.isLast());
        }
        return "/admin/orders"; // Trả về trang admin/orders.html

    }
}
