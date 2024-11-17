package com.ecom.util;

import com.ecom.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;

    public  Boolean sendMail(String url,String reciepentEmail) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("phathuynh897@gmai.com","Admin");
        helper.setTo(reciepentEmail);

        String content = "<p>Xin chào,</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu của mình.</p>"
                + "<p>Nhấp vào liên kết bên dưới để thay đổi mật khẩu của bạn:</p>"
                + "<p><a href=\"" + url + "\">Đổi mật khẩu của tôi</a></p>";
        helper.setSubject("Đặt lại mật khẩu");
        helper.setText(content, true);
        mailSender.send(message);
        return true;

    }

    public static String generateUrl(HttpServletRequest request) {
         String siteUrl = request.getRequestURL().toString();

        return siteUrl.replace(request.getServletPath(),"");

    }


}
