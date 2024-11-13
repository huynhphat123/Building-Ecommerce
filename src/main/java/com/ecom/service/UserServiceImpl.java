package com.ecom.service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserReponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired // Tự động tiêm (inject) repository vào service này
    private UserReponsitory userReponsitory;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDtls saveUser(UserDtls user) {

        user.setRole("ROLE_USER");
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        // Lưu thông tin người dùng vào cơ sở dữ liệu thông qua repository
        UserDtls saveUser = userReponsitory.save(user);
        return saveUser;
    }
}
