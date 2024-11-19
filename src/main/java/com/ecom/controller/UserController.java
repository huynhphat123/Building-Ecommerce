package com.ecom.controller;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.OrderRequest;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")  // Định tuyến tất cả các yêu cầu với tiền tố "/user"
public class UserController {

    @Autowired
    private UserService userService;  // Dịch vụ xử lý người dùng

    @Autowired
    private CategoryService categoryService;  // Dịch vụ xử lý danh mục sản phẩm

    @Autowired
    private CartService cartService;  // Dịch vụ xử lý giỏ hàng

    @Autowired
    private OrderService orderService; // Dịch vụ xử lí đặt hàng

    // Trang chủ của người dùng
    @GetMapping("/")
    public String home() {
        return "user/home";  // Trả về trang home của người dùng
    }

    // Phương thức @ModelAttribute để lấy thông tin người dùng và danh mục
    @ModelAttribute
    public void getUserDetails(Principal p, Model model) {
        if (p != null) {  // Kiểm tra nếu người dùng đã đăng nhập
            String email = p.getName();  // Lấy email người dùng từ Principal
            UserDtls userDtls = userService.getUserByEmail(email);  // Lấy thông tin người dùng từ email
            model.addAttribute("user", userDtls);  // Thêm thông tin người dùng vào model
            Integer countCart = cartService.getCountCart(userDtls.getId());  // Lấy số lượng sản phẩm trong giỏ hàng
            model.addAttribute("countCart", countCart);  // Thêm số lượng giỏ hàng vào model
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();  // Lấy tất cả danh mục sản phẩm đang hoạt động
        model.addAttribute("categorys", allActiveCategory);  // Thêm danh mục vào model
    }

    // Thêm sản phẩm vào giỏ hàng
    @GetMapping("/addCart")
    public String addCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {

        // Lưu giỏ hàng cho sản phẩm và người dùng
        Cart saveCart = cartService.saveCart(pid, uid);

        // Kiểm tra nếu giỏ hàng lưu thành công
        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Thêm sản phẩm vào giỏ hàng không thành công");
        } else {
            session.setAttribute("succMsg", "Sản phẩm đã được thêm vào giỏ hàng");
        }
        return "redirect:/product/" + pid;  // Chuyển hướng về trang chi tiết sản phẩm
    }

    // Hiển thị trang giỏ hàng của người dùng
    @GetMapping("/cart")
    public String loadCartPage(Principal principal, Model model) {

        // Lấy thông tin người dùng đã đăng nhập
        UserDtls user = getLoggedInUserDetails(principal);

        // Lấy danh sách các sản phẩm trong giỏ hàng của người dùng
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);  // Thêm giỏ hàng vào model

        // Tính tổng giá trị đơn hàng
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);  // Thêm tổng giá trị đơn hàng vào model
        }

        return "/user/cart";  // Trả về trang giỏ hàng của người dùng
    }

    // Cập nhật số lượng sản phẩm trong giỏ hàng
    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        // Cập nhật số lượng sản phẩm trong giỏ hàng
        cartService.updateQuantity(sy, cid);
        return "redirect:/user/cart";  // Chuyển hướng về trang giỏ hàng
    }

    // Phương thức trợ giúp lấy thông tin người dùng đã đăng nhập
    private UserDtls getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();  // Lấy email của người dùng
        UserDtls userDtls = userService.getUserByEmail(email);  // Lấy thông tin người dùng từ email
        return userDtls;
    }

    @GetMapping("/orders")
    public String orderPage(Principal principal, Model model) {
        UserDtls user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 2500 + 500;
            model.addAttribute("orderPrice", orderPrice);
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal principal){
        UserDtls user = getLoggedInUserDetails(principal);
        orderService.saveOrder(user.getId(),request);

        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess() {
        return "/user/success";
    }
}

