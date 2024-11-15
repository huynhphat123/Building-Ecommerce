package com.ecom.config;

import com.ecom.model.UserDtls;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CustomUser implements UserDetails {

    private UserDtls user;

    public CustomUser(UserDtls user) {
        super();
        this.user = user;
    }

    // Lấy ra quyền hạn (vai trò) của người dùng (ROLE_USER hoặc ROLE_ADMIN)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return Arrays.asList(authority);
    }

    // Lấy mật khẩu của người dùng
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // Lấy tên đăng nhập (email) của người dùng
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Kiểm tra tài khoản có hết hạn hay không
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // Kiểm tra tài khoản có bị khóa hay không
    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNonLocked();
    }

    // Kiểm tra mật khẩu có hết hạn hay không
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Kiểm tra tài khoản có đang hoạt động hay không
    @Override
    public boolean isEnabled() {
        return user.getIsEnable();
    }
}



