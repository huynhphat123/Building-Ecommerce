package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

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
    public String products() {
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
