package com.ecom.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Service
public class CommonServiceImpl implements CommonService {

    @Override
    public void removeSessionMessage() {
        // Lấy HttpServletRequest từ session hiện tại
       HttpServletRequest request =  ((ServletRequestAttributes)(Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))).getRequest();
        HttpSession session = request.getSession(); // Lấy session từ request

        // Xóa các thông báo từ session
        session.removeAttribute(("succMsg"));
        session.removeAttribute(("errorMsg"));

    }
}
