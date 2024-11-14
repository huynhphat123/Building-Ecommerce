package com.ecom.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

@Service
public class AuthSucessHandlerImpl implements AuthenticationSuccessHandler {

    // Phương thức này được gọi sau khi đăng nhập thành công
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Lấy ra danh sách quyền hạn của người dùng
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // Chuyển đổi danh sách quyền hạn thành tập hợp các vai trò
        Set<String> roles = AuthorityUtils.authorityListToSet(authorities);

        // Nếu người dùng có vai trò là "ROLE_ADMIN" thì chuyển hướng đến trang admin
        if(roles.contains("ROLE_ADMIN"))
        {
            response.sendRedirect("/admin/");
        }else {
            // Nếu không, thì chuyển hướng đến trang người dùng
            response.sendRedirect("/");
        }
    }
}
