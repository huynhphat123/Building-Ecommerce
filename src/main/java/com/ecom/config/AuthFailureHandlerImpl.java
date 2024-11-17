package com.ecom.config;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserReponsitory;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private UserReponsitory userReponsitory;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        String email = request.getParameter("username");

        UserDtls userDtls = userReponsitory.findByEmail(email);

        if (userDtls != null) {

            if (userDtls.getIsEnable()) {

                if (userDtls.getAccountNonLocked()) {

                    // Nếu số lần nhập sai ít hơn giới hạn, tăng số lần nhập sai
                    if (userDtls.getFaildAttempts() < AppConstant.ATTEMPT_TIME) {
                        userService.increaseFaildAttempt(userDtls);
                    } else {
                        // Nếu vượt quá giới hạn, khóa tài khoản
                        userService.userAccountLock(userDtls);
                        exception = new LockedException("\"Tài khoản của bạn đã bị khóa do nhập sai 3 lần\"");
                    }
                } else {
                    // Kiểm tra nếu tài khoản bị khóa, xác định xem đã hết thời gian khóa chưa
                    if (userService.unlockAccountTimeExpired(userDtls)) {
                        exception = new LockedException("Tài khoản của bạn đã được mở khóa,Vui lòng thử đăng nhập");
                    } else {
                        exception = new LockedException("Tài khoản của bạn đã bị khóa!! Vui lòng đợi hỗ trợ");
                    }
                }

            } else {
                exception = new LockedException("Tài khoản của bạn không hoạt động");
            }
        } else {
            exception = new LockedException("Email hoặc mật khẩu không hợp lệ. Vui lòng thử lại.");
        }

        // Thiết lập URL trả về khi đăng nhập thất bại
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }
}
