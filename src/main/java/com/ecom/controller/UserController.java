package com.ecom.controller;

import com.ecom.model.Cart;
import com.ecom.model.Category;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping ("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String home() {
        return "user/home";
    }

    @ModelAttribute
    public void getUserDetails(Principal p, Model model) {
        if (p != null) {
            String email = p.getName();
            UserDtls userDtls = userService.getUserByEmail(email);
            model.addAttribute("user", userDtls);
            Integer countCart = cartService.getCountCart(userDtls.getId());
            model.addAttribute("countCart", countCart);
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("categorys", allActiveCategory);
    }

    @GetMapping("/addCart")
    public String addCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session) {

        Cart saveCart = cartService.saveCart(pid, uid);

        if (ObjectUtils.isEmpty(saveCart)) {
            session.setAttribute("errorMsg", "Thêm sản phẩm vào giỏ hàng không thành công");
        } else {
            session.setAttribute("succMsg", "Sản phẩm đã được thêm vào giỏ hàng");
        }
        return "redirect:/product/" + pid;
    }

    @GetMapping("/cart")
    public String loadCartPage(Principal principal,Model model) {

        UserDtls user = getLoggedInUserDetails(principal);
        List<Cart> carts = cartService.getCartsByUser(user.getId());
        model.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            model.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
        cartService.updateQuantity(sy, cid);
        return "redirect:/user/cart";
    }

    private UserDtls getLoggedInUserDetails(Principal principal) {
        String email = principal.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

}
