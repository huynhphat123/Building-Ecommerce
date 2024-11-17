package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
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
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtil commonUtil;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

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
    public String products(Model model, @RequestParam(value = "category",defaultValue = "") String category) {
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts(category);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("paramValue",category);
        return "product"; // Trả về product.html trong /templates/
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id,Model model) {
        Product productById = productService.getProductById(id);
        model.addAttribute("product", productById);
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

        UserDtls userByEmail = userService.getUserByEmail(email);
        if (ObjectUtils.isEmpty(userByEmail)) {
            session.setAttribute("errorMsg", "Email không tồn tại");
        } else {

            String resetToken = UUID.randomUUID().toString();
            userService.updateUserResetToken(email,resetToken);

            // Generate URL:http://localhost:8081/reset-password?token=sdaskdjaskjdaksjdas
            String url = CommonUtil.generateUrl(request) + "/reset-password?token=" + resetToken;

            Boolean sendMail = commonUtil.sendMail(url,email);
            if (sendMail) {
                session.setAttribute("succMsg", "Vui lòng kiểm tra email của bạn. Đường dẫn đặt lại mật khẩu đã được gửi");
            } else {
                session.setAttribute("errorMsg", "Có lỗi xảy ra trên máy chủ | Email không được gửi");
            }
        }
        return "redirect:/forgot-password";

    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, HttpSession session, Model model) {
        UserDtls userByToken = userService.getUserByToken(token);

        if (userByToken == null) {
            model.addAttribute("errorMsg", "Liên kết của bạn không hợp lệ hoặc đã hết hạn");
            return "message";
        }
        model.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session, Model model) {

        UserDtls userByToken = userService.getUserByToken(token);
        if (userByToken == null) {
            model.addAttribute("errorMsg", "Liên kết của bạn không hợp lệ hoặc đã hết hạn");
            return "message";
        } else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            model.addAttribute("msg", "Đổi mật khẩu thành công");
            return "message";
        }
    }
}
