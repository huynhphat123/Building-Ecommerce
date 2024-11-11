package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index() {
        return "index"; // Trả về index.html trong /templates/
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // Trả về login.html trong /templates/
    }


    @GetMapping("/register")
    public String register() {
        return "register"; // Trả về register.html trong /templates/
    }

    @GetMapping("/products")
    public String products(Model model) {
        List<Category> categories = categoryService.getAllActiveCategory();
        List<Product> products = productService.getAllActiveProducts();
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "product"; // Trả về product.html trong /templates/
    }

    @GetMapping("/product")
    public String product() {
        return "view_product"; // Trả về product.html trong /templates/
    }

    @GetMapping("/category")
    public String category() {
        return "category"; // Trả về category.html trong /templates/
    }
}
