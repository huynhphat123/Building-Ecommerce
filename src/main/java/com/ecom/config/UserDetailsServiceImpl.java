package com.ecom.config;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserReponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserReponsitory userReponsitory;

    // Phương thức này tìm người dùng dựa vào email, và trả về thông tin người dùng
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Tìm người dùng trong database theo email
        UserDtls user = userReponsitory.findByEmail(username);

        // Nếu không tìm thấy, ném ra ngoại lệ
        if(user == null) {
            throw new UsernameNotFoundException("Người dùng không tìm thấy");
        }
        // Nếu tìm thấy, trả về đối tượng CustomUser (chứa thông tin người dùng và vai trò)
        return new CustomUser(user);
    }
}

