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

    @GetMapping("/")  // Xử lý yêu cầu GET đến trang chủ "/"
    public String index(Model model) {

        // Lấy danh sách các thể loại (category) đang hoạt động từ service
        // Sắp xếp theo ID giảm dần và lấy 6 danh mục mới nhất
        List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
                .sorted((c1, c2) -> c2.getId().compareTo(c1.getId())) // Sắp xếp theo ID giảm dần
                .limit(6)  // Giới hạn số lượng thể loại là 6
                .toList();  // Chuyển thành danh sách

        // Lấy danh sách các sản phẩm (product) đang hoạt động từ service
        // Sắp xếp theo ID giảm dần và lấy 12 sản phẩm mới nhất
        List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
                .sorted((p1, p2) -> p2.getId().compareTo(p1.getId())) // Sắp xếp theo ID giảm dần
                .limit(12)  // Giới hạn số lượng sản phẩm là 8
                .toList();  // Chuyển thành danh sách

        // Thêm dữ liệu thể loại vào model để truyền vào view
        model.addAttribute("category", allActiveCategory);

        // Thêm dữ liệu sản phẩm vào model để truyền vào view
        model.addAttribute("products", allActiveProducts);

        // Trả về tên view là "index", nơi dữ liệu sẽ được hiển thị
        return "index";
    }


    @GetMapping("/signin")
    public String login() {
        return "login"; // Trả về login.html trong /templates/
    }


    @GetMapping("/register")
    public String register() {
        return "register"; // Trả về register.html trong /templates/
    }

    @GetMapping("/products")  // Xử lý yêu cầu GET đến trang "/products" (hiển thị danh sách sản phẩm)
    public String products(Model model,
                           @RequestParam(value = "category", defaultValue = "") String category,  // Lấy tham số category từ URL, mặc định là chuỗi rỗng
                           @RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,  // Lấy tham số pageNo từ URL (trang hiện tại), mặc định là trang 0
                           @RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize,  // Lấy tham số pageSize từ URL (số lượng sản phẩm mỗi trang), mặc định là 12
                           @RequestParam(defaultValue = "") String ch) {  // Lấy tham số tìm kiếm ch từ URL, mặc định là chuỗi rỗng

        // Lấy danh sách các thể loại đang hoạt động và truyền vào model để hiển thị ở view
        List<Category> categories = categoryService.getAllActiveCategory();
        model.addAttribute("categories", categories);

        // Truyền tham số category vào model để sử dụng trong view (dành cho việc lọc theo thể loại)
        model.addAttribute("paramValue", category);

        // Khai báo đối tượng Page<Product> để chứa danh sách sản phẩm phân trang
        Page<Product> page = null;

        // Nếu tham số tìm kiếm "ch" rỗng, lấy tất cả sản phẩm theo phân trang
        if (StringUtils.isEmpty(ch)) {
            page = productService.getAllActiveProductPagination(pageNo, pageSize, category);  // Lấy sản phẩm theo phân trang và thể loại
        } else {
            // Nếu có tham số tìm kiếm "ch", thực hiện tìm kiếm sản phẩm theo phân trang và thể loại
            page = productService.searchActiveProductPagination(pageNo, pageSize, category, ch);  // Tìm kiếm sản phẩm theo tên
        }

        // Lấy danh sách sản phẩm từ đối tượng Page
        List<Product> products = page.getContent();

        // Truyền danh sách sản phẩm vào model để hiển thị
        model.addAttribute("products", products);

        // Truyền số lượng sản phẩm vào model
        model.addAttribute("productsSize", products.size());

        // Truyền thông tin phân trang vào model
        model.addAttribute("pageNo", page.getNumber());  // Số trang hiện tại
        model.addAttribute("pageSize", pageSize);  // Số lượng sản phẩm mỗi trang
        model.addAttribute("totalElements", page.getTotalElements());  // Tổng số sản phẩm
        model.addAttribute("totalPages", page.getTotalPages());  // Tổng số trang
        model.addAttribute("isFirst", page.isFirst());  // Kiểm tra nếu đây là trang đầu tiên
        model.addAttribute("isLast", page.isLast());  // Kiểm tra nếu đây là trang cuối cùng

        // Trả về tên view (trang web) sẽ hiển thị dữ liệu, ở đây là "product"
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
    public String userUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session) throws IOException {

        Boolean existsEmail = userService.existsEmail(user.getEmail());

        if(existsEmail) {
            session.setAttribute("errorMsg", "Email đã tồn tại");
        } else {
            // Kiểm tra nếu file ảnh được tải lên trống, đặt tên ảnh mặc định là "default.jpg"
            String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
            // Gán tên file ảnh vào thuộc tính profileImage của đối tượng userDtls
            user.setProfileImage(imageName);
            // Lưu thông tin người dùng
            UserDtls saveUser = userService.saveUser(user);

            // Nếu người dùng được lưu thành công
            if (!ObjectUtils.isEmpty(saveUser)) {
                // Nếu file ảnh không trống
                if (!file.isEmpty()) {
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

    // Xử lý yêu cầu tìm kiếm sản phẩm
    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model model) {
        // Gọi service để tìm kiếm sản phẩm theo tiêu đề hoặc danh mục
        List<Product> searchProducts = productService.searchProduct(ch);

        // Thêm danh sách sản phẩm tìm được vào model để hiển thị trên trang
        model.addAttribute("products", searchProducts);

        // Gọi service để lấy tất cả danh mục đang hoạt động
        List<Category> categories = categoryService.getAllActiveCategory();

        // Thêm danh sách danh mục vào model để hiển thị trên trang
        model.addAttribute("categories", categories);

        return "product"; // Trả về trang product.html để hiển thị kết quả tìm kiếm
    }

}
