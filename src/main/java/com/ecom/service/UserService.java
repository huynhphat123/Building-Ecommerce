package com.ecom.service;

import com.ecom.model.UserDtls;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    // Lưu thông tin người dùng vào cơ sở dữ liệu
    public UserDtls saveUser(UserDtls userDtls);

    // Lấy thông tin người dùng bằng email
    public UserDtls getUserByEmail(String email);

    // Lấy danh sách người dùng theo vai trò (role)
    public List<UserDtls> getUsers(String role);

    // Cập nhật trạng thái hoạt động của tài khoản người dùng (bật/tắt tài khoản)
    Boolean updateAccountStatus(Integer id, Boolean status);

    // Tăng số lần nhập sai mật khẩu của người dùng
    public void increaseFaildAttempt(UserDtls user);

    // Khóa tài khoản người dùng sau khi vượt quá số lần nhập sai mật khẩu
    public void userAccountLock(UserDtls user);

    // Kiểm tra thời gian khóa tài khoản đã hết chưa và mở khóa nếu đã hết thời gian
    public boolean unlockAccountTimeExpired(UserDtls user);

    // Đặt lại số lần nhập sai của người dùng về 0
    public void resetAttempt(int userId);

    // Cập nhật token đặt lại mật khẩu (resetToken) cho người dùng
    // Phương thức này cập nhật token reset mật khẩu cho người dùng khi họ yêu cầu thay đổi mật khẩu.
    void updateUserResetToken(String email, String resetToken);

    // Lấy thông tin người dùng dựa trên reset token
    // Phương thức này tìm người dùng dựa trên token đặt lại mật khẩu.
    public UserDtls getUserByToken(String token);

    // Cập nhật thông tin người dùng
    // Phương thức này dùng để cập nhật thông tin của người dùng, ví dụ như tên, địa chỉ, số điện thoại.
    public UserDtls updateUser(UserDtls user);

    // Cập nhật hồ sơ người dùng, bao gồm việc thay đổi thông tin và hình ảnh
    // Phương thức này dùng để cập nhật thông tin người dùng và hình ảnh đại diện của họ.
    public UserDtls updateUserProfile(UserDtls user, MultipartFile img);

    // Lưu thông tin người dùng mới với quyền "admin"
    // Phương thức này được sử dụng khi tạo tài khoản quản trị viên (admin) mới.
    // Cập nhật vai trò là "ROLE_ADMIN", mật khẩu được mã hóa và tài khoản mặc định được kích hoạt.
    public UserDtls saveAdmin(UserDtls user);
}
