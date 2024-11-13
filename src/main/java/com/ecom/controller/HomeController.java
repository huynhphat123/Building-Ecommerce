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
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

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
}
