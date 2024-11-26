package com.ecom.service;

import com.ecom.model.Product;
import org.springframework.data.domain.Page;
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

    // Lấy tất cả sản phẩm đang hoạt động theo danh mục (nếu có)
    public List<Product> getAllActiveProducts(String category);

    // Tìm kiếm sản phẩm theo tiêu đề hoặc danh mục
    public List<Product> searchProduct(String ch);

    // Lấy sản phẩm đang hoạt động và phân trang theo danh mục
    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);

    // Tìm kiếm sản phẩm theo tiêu đề hoặc danh mục và phân trang
    public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch);

    // Lấy tất cả sản phẩm và phân trang
    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize);

}

