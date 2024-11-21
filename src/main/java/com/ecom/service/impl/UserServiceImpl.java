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
        return userReponsitory.findByEmail(email);
    }

    @Override
    public List<UserDtls> getUsers(String role) {
       return userReponsitory.findByRole(role);
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {

        Optional<UserDtls> findByUser = userReponsitory.findById(id);

        if(findByUser.isPresent()) {
            UserDtls userDtls = findByUser.get();
            userDtls.setIsEnable(status);
            userReponsitory.save(userDtls);
            return true;
        }

        return false;
    }

    @Override
    public void increaseFaildAttempt(UserDtls user) {
        int attempt = user.getFaildAttempts() + 1;
        user.setFaildAttempts(attempt);
        userReponsitory.save(user);
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

    }

    @Override
    public void updateUserResetToken(String email, String resetToken) {
        UserDtls findByEmail = userReponsitory.findByEmail(email);
        findByEmail.setResetToken(resetToken);
        userReponsitory.save(findByEmail);
    }

    @Override
    public UserDtls getUserByToken(String token) {
        return userReponsitory.findByResetToken(token);

    }

    @Override
    public UserDtls updateUser(UserDtls user) {
        return userReponsitory.save(user);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls user, MultipartFile img) {

        UserDtls dbUser = userReponsitory.findById(user.getId()).get();

        if (!img.isEmpty()) {
            dbUser.setProfileImage(img.getOriginalFilename());
        }

        if (!ObjectUtils.isEmpty(dbUser)) {

            dbUser.setName(user.getName());
            dbUser.setNumber(user.getNumber());
            dbUser.setAddress(user.getAddress());
            dbUser.setCity(user.getCity());

            dbUser = userReponsitory.save(dbUser);
        }
        try {
            if (!img.isEmpty()) {
                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                        + img.getOriginalFilename());

                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbUser;
    }


}
