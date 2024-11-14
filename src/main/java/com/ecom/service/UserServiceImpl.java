package com.ecom.service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserReponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


}
