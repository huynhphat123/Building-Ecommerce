package com.ecom.service.impl;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;
import com.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service  // Đánh dấu lớp này là một service trong Spring để Spring biết đây là một component được quản lý bởi Spring container
public class ProductServiceImpl implements ProductService {

    @Autowired  // Tự động tiêm dependency vào productRepository từ Spring context
    private ProductRepository productRepository;

    // Phương thức lưu sản phẩm vào cơ sở dữ liệu
    @Override
    public Product saveProduct(Product product) {
        return productRepository.save(product); // Lưu sản phẩm vào cơ sở dữ liệu và trả về đối tượng sản phẩm đã lưu
    }

    // Phương thức lấy danh sách tất cả sản phẩm từ cơ sở dữ liệu
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll(); // Trả về danh sách tất cả sản phẩm từ cơ sở dữ liệu
    }

    // Phương thức xóa sản phẩm theo ID
    @Override
    public Boolean deleteProduct(Integer id) {
        // Tìm sản phẩm theo ID
        Product product = productRepository.findById(id).orElse(null);
        if (!ObjectUtils.isEmpty(product)) { // Kiểm tra nếu sản phẩm tồn tại trong cơ sở dữ liệu
            // Nếu sản phẩm tồn tại, xóa sản phẩm khỏi cơ sở dữ liệu
            productRepository.delete(product);
            return true; // Trả về true nếu xóa thành công
        }
        return null; // Trả về null nếu không tìm thấy sản phẩm
    }

    // Phương thức tìm sản phẩm theo ID
    @Override
    public Product getProductById(Integer id) {
        // Tìm sản phẩm theo ID và trả về sản phẩm nếu tìm thấy, ngược lại trả về null
        return productRepository.findById(id).orElse(null);
    }

    // Phương thức cập nhật sản phẩm
    @Override
    public Product updateProduct(Product product, MultipartFile image) {
        // Lấy sản phẩm từ cơ sở dữ liệu theo ID
        Product dbProduct = getProductById(product.getId());
        // Nếu không có hình ảnh mới, giữ lại tên hình ảnh cũ
        String imageName = image.isEmpty() ? dbProduct.getImage() : image.getOriginalFilename();

        // Cập nhật các thông tin của sản phẩm
        dbProduct.setTitle(product.getTitle());  // Cập nhật tiêu đề sản phẩm
        dbProduct.setDescription(product.getDescription());  // Cập nhật mô tả sản phẩm
        dbProduct.setCategory(product.getCategory()); // Cập nhật danh mục sản phẩm
        dbProduct.setPrice(product.getPrice());  // Cập nhật giá sản phẩm
        dbProduct.setStock(product.getStock());  // Cập nhật số lượng sản phẩm trong kho
        dbProduct.setImage(imageName);  // Cập nhật tên hình ảnh sản phẩm
        dbProduct.setIsActive(product.getIsActive());
        dbProduct.setDiscount(product.getDiscount());

        Double discount = product.getPrice() * (product.getDiscount() / 100.0);
        Double discountPrice = product.getPrice() - discount;
        dbProduct.setDiscountPrice(discountPrice);

        // Lưu lại sản phẩm đã cập nhật
        Product updateProduct = productRepository.save(dbProduct);

        if (!ObjectUtils.isEmpty(updateProduct)) {  // Kiểm tra nếu sản phẩm đã được lưu thành công
            // Nếu có hình ảnh mới, lưu hình ảnh vào thư mục static
            if (!image.isEmpty()) {
                try {
                    // Lấy đường dẫn thư mục lưu ảnh
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
                    // Lưu file hình ảnh vào thư mục
                    Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();  // In ra lỗi nếu có lỗi xảy ra khi lưu hình ảnh
                }
            }
            return updateProduct;  // Trả về sản phẩm đã được cập nhật
        }
        return null;  // Trả về null nếu không cập nhật thành công
    }

    @Override
    public List<Product> getAllActiveProducts(String category) {
        List<Product> products = null;

        // Nếu không có danh mục được truyền vào, tìm tất cả sản phẩm đang hoạt động
        if (ObjectUtils.isEmpty(category)) {
            products = productRepository.findByIsActiveTrue();
        } else {
            // Nếu có danh mục, tìm sản phẩm theo danh mục
            products = productRepository.findByCategory(category);
        }

        return products; // Trả về danh sách sản phẩm theo điều kiện tìm kiếm
    }

    @Override
    public List<Product> searchProduct(String ch) {
        // Tìm sản phẩm theo tiêu đề hoặc danh mục có chứa chuỗi tìm kiếm, không phân biệt hoa thường
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
    }

    @Override
    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {

        // Tạo đối tượng Pageable dựa trên số trang và kích thước trang
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Product> pageProduct = null;

        // Nếu danh mục rỗng, lấy tất cả sản phẩm đang hoạt động, nếu không thì lọc theo danh mục
        if (ObjectUtils.isEmpty(category)) {
            pageProduct = productRepository.findByIsActiveTrue(pageable);
        } else {
            pageProduct = productRepository.findByCategory(pageable, category);
        }
        // Trả về danh sách sản phẩm đã phân trang
        return pageProduct;
    }

    @Override
    public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {

        // Tạo đối tượng Pageable dựa trên số trang và kích thước trang
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // Tìm kiếm sản phẩm theo tiêu đề hoặc danh mục và phân trang
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);
    }

    @Override
    public Page<Product> getAllProductsPagination(Integer pageNo, Integer pageSize) {

        // Tạo đối tượng Pageable dựa trên số trang và kích thước trang
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // Lấy tất cả sản phẩm và phân trang
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {
        // Khai báo đối tượng Page để lưu kết quả phân trang
        Page<Product> pageProduct = null;

        // Tạo đối tượng Pageable với số trang và kích thước trang, giúp phân trang dữ liệu
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        // Truy vấn dữ liệu sản phẩm với các điều kiện:
        // - Sản phẩm phải đang hoạt động (isActive = true)
        // - Tên sản phẩm (title) hoặc danh mục (category) chứa chuỗi tìm kiếm (ch), không phân biệt chữ hoa hay chữ thường
        pageProduct = productRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);

        // Trả về đối tượng Page chứa các sản phẩm theo các điều kiện trên
        return pageProduct;
    }

}


