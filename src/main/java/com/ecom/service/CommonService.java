package com.ecom.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public interface CommonService {

    // Phương thức để xóa thông báo trong session
    public void removeSessionMessage();


}
