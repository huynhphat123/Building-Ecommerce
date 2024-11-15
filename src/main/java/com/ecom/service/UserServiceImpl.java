package com.ecom.service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserReponsitory;
import com.ecom.util.AppConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


}
