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
        // Đặt vai trò của người dùng từ thông tin được lưu trong database
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        return Arrays.asList(authority);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Các phương thức kiểm tra trạng thái tài khoản (không hết hạn, không bị khóa, v.v.)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsEnable();
    }
}


