package com.ecom.service.impl;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserReponsitory;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired // Tự động tiêm (inject) repository vào service này
    private UserReponsitory userReponsitory;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Phương thức lưu thông tin người dùng, mặc định là ROLE_USER
    @Override
    public UserDtls saveUser(UserDtls user) {
        // Đặt vai trò mặc định cho người dùng là "ROLE_USER"
        user.setRole("ROLE_USER");
        user.setIsEnable(true);
        user.setAccountNonLocked(true);
        user.setFaildAttempts(0);
        // Mã hóa mật khẩu trước khi lưu vào database
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        // Lưu thông tin người dùng vào cơ sở dữ liệu
        UserDtls saveUser = userReponsitory.save(user);
        return saveUser;
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        // Trả về người dùng dựa trên email
        return userReponsitory.findByEmail(email);
    }

    @Override
    public List<UserDtls> getUsers(String role) {
        // Lấy danh sách người dùng theo vai trò (role) như "ROLE_ADMIN", "ROLE_USER"
        return userReponsitory.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {
        // Cập nhật trạng thái tài khoản của người dùng (enable/disable)
        Optional<UserDtls> findByUser = userReponsitory.findById(id);

        if(findByUser.isPresent()) {
            UserDtls userDtls = findByUser.get();
            userDtls.setIsEnable(status);  // Cập nhật trạng thái tài khoản
            userReponsitory.save(userDtls);  // Lưu lại vào cơ sở dữ liệu
            return true;
        }

        return false; // Trả về false nếu không tìm thấy người dùng
    }

    @Override
    public void increaseFaildAttempt(UserDtls user) {
        // Tăng số lần thử đăng nhập thất bại của người dùng
        int attempt = user.getFaildAttempts() + 1;
        user.setFaildAttempts(attempt);  // Cập nhật số lần thất bại
        userReponsitory.save(user);  // Lưu lại thay đổi
    }

    @Override
    public void userAccountLock(UserDtls user) {
        // Khi tài khoản bị khóa, đặt trạng thái khóa và lưu thời gian khóa
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userReponsitory.save(user);
    }

    @Override
    public boolean unlockAccountTimeExpired(UserDtls user) {
        // Kiểm tra nếu thời gian khóa đã hết thì mở khóa tài khoản
        long lockTime = user.getLockTime().getTime();
        long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;

        long currentTime = System.currentTimeMillis();

        if (unLockTime < currentTime) {
            user.setAccountNonLocked(true);
            user.setFaildAttempts(0); // Reset số lần nhập sai về 0
            user.setLockTime(null);
            userReponsitory.save(user);
            return true;
        }

        return false;
    }

    @Override
    public void resetAttempt(int userId) {
        // Reset số lần thử đăng nhập thất bại của người dùng
    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        // Cập nhật token đặt lại mật khẩu cho người dùng dựa trên email
        UserDtls findByEmail = userReponsitory.findByEmail(email);
        findByEmail.setResetToken(resetToken);  // Cập nhật token
        userReponsitory.save(findByEmail);  // Lưu lại vào cơ sở dữ liệu
    }

    @Override
    public UserDtls getUserByToken(String token) {
        // Lấy thông tin người dùng dựa trên reset token
        return userReponsitory.findByResetToken(token);

    }

    @Override
    public UserDtls updateUser(UserDtls user) {
        // Cập nhật thông tin người dùng, bao gồm tên, email, mật khẩu, v.v.
        return userReponsitory.save(user);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls user, MultipartFile img) {
        // Cập nhật hồ sơ người dùng, bao gồm cả ảnh đại diện nếu có
        UserDtls dbUser = userReponsitory.findById(user.getId()).get();

        if (!img.isEmpty()) {
            dbUser.setProfileImage(img.getOriginalFilename());  // Cập nhật ảnh đại diện
        }

        if (!ObjectUtils.isEmpty(dbUser)) {
            dbUser.setName(user.getName());  // Cập nhật tên
            dbUser.setNumber(user.getNumber());  // Cập nhật số điện thoại
            dbUser.setAddress(user.getAddress());  // Cập nhật địa chỉ
            dbUser.setCity(user.getCity());  // Cập nhật thành phố

            dbUser = userReponsitory.save(dbUser);  // Lưu lại thay đổi
        }

        try {
            if (!img.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + img.getOriginalFilename());

                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);  // Lưu ảnh vào thư mục
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbUser;
    }

    @Override
    public UserDtls saveAdmin(UserDtls user) {
        // Lưu người dùng mới với quyền admin
        user.setRole("ROLE_ADMIN");  // Gán quyền admin cho người dùng
        user.setIsEnable(true);  // Kích hoạt tài khoản
        user.setAccountNonLocked(true);  // Tài khoản không bị khóa
        user.setFaildAttempts(0);  // Đặt số lần thử đăng nhập thất bại ban đầu là 0

        String encodePassword = passwordEncoder.encode(user.getPassword());  // Mã hóa mật khẩu
        user.setPassword(encodePassword);  // Cập nhật mật khẩu đã mã hóa

        UserDtls saveUser = userReponsitory.save(user);  // Lưu người dùng vào cơ sở dữ liệu
        return saveUser;
    }

}
