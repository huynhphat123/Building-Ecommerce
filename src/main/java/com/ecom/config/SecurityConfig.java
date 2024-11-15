package com.ecom.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    @Lazy
    private AuthFailureHandlerImpl authenticationFailureHandler;

    // Bean mã hóa mật khẩu (sử dụng BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình dịch vụ UserDetails để lấy thông tin người dùng từ database
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    // Cấu hình phương thức xác thực sử dụng DaoAuthenticationProvider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    // Cấu hình bảo mật của ứng dụng
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
    {
        // Tắt CSRF và CORS, cấu hình quyền truy cập các URL theo vai trò
        http.csrf(csrf->csrf.disable()).cors(cors->cors.disable())
                .authorizeHttpRequests(req->req.requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/**").permitAll())
                // Cấu hình đăng nhập và đăng xuất
                .formLogin(form->form.loginPage("/signin")
                        .loginProcessingUrl("/login")
                        .failureHandler(authenticationFailureHandler) // Xử lý thất bại đăng nhập
                        .successHandler(authenticationSuccessHandler)) // Xử lý thành công đăng nhập
                .logout(logout->logout.permitAll());

        return http.build();
    }
}

