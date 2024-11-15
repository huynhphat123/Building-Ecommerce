package com.ecom.service;

import com.ecom.model.UserDtls;

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
}
