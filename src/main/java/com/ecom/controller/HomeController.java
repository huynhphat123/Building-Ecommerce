package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;  // Dịch vụ xử lý các danh mục sản phẩm

    @Autowired
    private ProductService productService;  // Dịch vụ xử lý các sản phẩm

    @Autowired
    private UserService userService;  // Dịch vụ xử lý người dùng

    @Autowired
    private CommonUtil commonUtil;  // Các hàm tiện ích chung

    @Autowired
    private PasswordEncoder passwordEncoder;  // Mã hóa mật khẩu

    @Autowired
    private CartService cartService;  // Dịch vụ xử lý giỏ hàng

    // Phương thức @ModelAttribute để lấy thông tin người dùng và danh mục
    @ModelAttribute
    public void getUserDetails(Principal p, Model model) {
        if (p != null) {  // Kiểm tra xem người dùng đã đăng nhập chưa
            String email = p.getName();  // Lấy email người dùng từ Principal
            UserDtls userDtls = userService.getUserByEmail(email);  // Lấy thông tin người dùng từ email
            model.addAttribute("user", userDtls);  // Thêm thông tin người dùng vào model
            Integer countCart = cartService.getCountCart(userDtls.getId());  // Lấy số lượng sản phẩm trong giỏ hàng
            model.addAttribute("countCart", countCart);  // Thêm số lượng giỏ hàng vào model
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();  // Lấy danh sách danh mục
        model.addAttribute("categorys", allActiveCategory);  // Thêm danh mục vào model
    }

    @GetMapping("/")
    public String index() {
        return "index"; // Trả về index.html trong /templates/
    }

    @GetMapping("/signin")
    public String login() {
        return "login"; // Trả về login.html trong /templates/
    }


    @GetMapping("/register")
    public String register() {
        return "register"; // Trả về register.html trong /templates/
    }

    @GetMapping("/products")
    public String products(Model model, @RequestParam(value = "category", defaultValue = "") String category) {

        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("paramValue", category);
        return "product";
    }


    @GetMapping("/product/{id}")
    public String product(@PathVariable int id,Model model) {
        Product productById = productService.getProductById(id);  // Lấy sản phẩm theo id
        model.addAttribute("product", productById);  // Thêm sản phẩm vào model
        return "view_product"; // Trả về product.html trong /templates/
    }

    @GetMapping("/category")
    public String category() {
        return "category"; // Trả về category.html trong /templates/
    }

    @PostMapping("/saveUser")
    public String userUser(@ModelAttribute UserDtls userDtls, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {

        // Kiểm tra nếu file ảnh được tải lên trống, đặt tên ảnh mặc định là "default.jpg"
        String imageName =  file.isEmpty() ? "default.jpg" : file.getOriginalFilename();

        // Gán tên file ảnh vào thuộc tính profileImage của đối tượng userDtls
        userDtls.setProfileImage(imageName);

        // Lưu thông tin người dùng
        UserDtls saveUser = userService.saveUser(userDtls);

        // Nếu người dùng được lưu thành công
        if(!ObjectUtils.isEmpty(saveUser))
        {
            // Nếu file ảnh không trống
            if(!file.isEmpty())
            {
                // Lấy đường dẫn của thư mục chứa ảnh trong dự án
                File saveFile = new ClassPathResource("static/img").getFile();

                // Tạo đường dẫn nơi lưu ảnh đại diện của người dùng
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + file.getOriginalFilename());

                // Sao chép file ảnh từ InputStream vào đường dẫn chỉ định
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            // Đặt thông báo thành công vào session
            session.setAttribute("succMsg", "Đăng ký thành công");
        } else {
            // Đặt thông báo lỗi vào session nếu không lưu được
            session.setAttribute("errorMsg", "Đăng ký thất bại");
        }
        // Redirect người dùng về trang đăng ký
        return "redirect:/register";
    }

    // Forgot Password code
    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "forgot_password.html";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        UserDtls userByEmail = userService.getUserByEmail(email);  // Lấy người dùng theo email
        if (ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Email không tồn tại");  // Thông báo lỗi nếu email không tồn tại
        } else {
            String resetToken = UUID.randomUUID().toString();  // Tạo token đặt lại mật khẩu
            userService.updateUserResetToken(email, resetToken);  // Cập nhật token cho người dùng
            String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;  // Tạo URL đặt lại mật khẩu

            Boolean sendMail = commonUtil.sendMail(url, email);  // Gửi email với đường dẫn đặt lại mật khẩu
            if (sendMail) {
                session.setAttribute("succMsg", "Vui lòng kiểm tra email của bạn. Đường dẫn đặt lại mật khẩu đã được gửi");
            } else {
                session.setAttribute("errorMsg", "Có lỗi xảy ra trên máy chủ | Email không được gửi");
            }
        }
        return "redirect:/forgot-password";  // Chuyển hướng về trang quên mật khẩu
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, HttpSession session, Model model) {
        UserDtls userByToken = userService.getUserByToken(token);  // Lấy người dùng theo token
        if (userByToken == null) {
            model.addAttribute("errorMsg", "Liên kết của bạn không hợp lệ hoặc đã hết hạn");
            return "message";  // Nếu token không hợp lệ, hiển thị thông báo lỗi
        }
        model.addAttribute("token", token);  // Thêm token vào model
        return "reset_password";  // Trả về trang reset_password.html
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session, Model model) {

        UserDtls userByToken = userService.getUserByToken(token);  // Lấy người dùng theo token
        if (userByToken == null) {
            model.addAttribute("errorMsg", "Liên kết của bạn không hợp lệ hoặc đã hết hạn");
            return "message";  // Nếu token không hợp lệ, hiển thị thông báo lỗi
        } else {
            userByToken.setPassword(passwordEncoder.encode(password));  // Mã hóa mật khẩu mới
            userByToken.setResetToken(null);  // Xóa token sau khi đã reset mật khẩu
            userService.updateUser(userByToken);  // Cập nhật thông tin người dùng
            model.addAttribute("msg", "Đổi mật khẩu thành công");  // Thông báo thành công
            return "message";  // Trả về trang message.html
        }
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch,Model model) {
        List<Product> searchProducts = productService.searchProduct(ch);
        model.addAttribute("products", searchProducts);
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);

        return "product";
    }
}
