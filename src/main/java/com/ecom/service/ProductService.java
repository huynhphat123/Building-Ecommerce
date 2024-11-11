package com.ecom.service;

import com.ecom.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    // Lưu sản phẩm vào cơ sở dữ liệu
    public Product saveProduct(Product product);

    // Lấy tất cả sản phẩm
    public List<Product> getAllProducts();

    // Xóa sản phẩm theo ID
    public Boolean deleteProduct(Integer id);

    // Lấy thông tin sản phẩm theo ID
    public Product getProductById(Integer id);

    // Cập nhật thông tin sản phẩm
    public Product updateProduct(Product product, MultipartFile file);

    public List<Product> getAllActiveProducts();
}
