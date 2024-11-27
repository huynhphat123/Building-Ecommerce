package com.ecom.util;

import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender; // Tự động inject JavaMailSender để gửi email

    @Autowired
    private UserService userService;


    public  Boolean sendMail(String url,String reciepentEmail) throws MessagingException, UnsupportedEncodingException {

        // Tạo một MimeMessage để gửi email
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        // Cài đặt thông tin người gửi
        helper.setFrom("phathuynh897@gmai.com", "Admin");
        // Cài đặt email người nhận
        helper.setTo(reciepentEmail);

        // Nội dung email bao gồm liên kết đặt lại mật khẩu
        String content = "<p>Xin chào,</p>"
                + "<p>Bạn đã yêu cầu đặt lại mật khẩu của mình.</p>"
                + "<p>Nhấp vào liên kết bên dưới để thay đổi mật khẩu của bạn:</p>"
                + "<p><a href=\"" + url + "\">Đổi mật khẩu của tôi</a></p>";

        // Tiêu đề email
        helper.setSubject("Đặt lại mật khẩu");

        // Nội dung email, thiết lập định dạng HTML
        helper.setText(content, true);

        // Gửi email
        mailSender.send(message);

        // Trả về true nếu thành công
        return true;
    }
    String msg=null;

    public static String generateUrl(HttpServletRequest request) {
        // Lấy URL đầy đủ từ yêu cầu hiện tại
        String siteUrl = request.getRequestURL().toString();

        // Loại bỏ đường dẫn cụ thể (servletPath) để lấy URL gốc
        return siteUrl.replace(request.getServletPath(), "");
    }
    public Boolean sendMailForProductOrder(ProductOrder order,String status) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        // Cài đặt thông tin người gửi
        helper.setFrom("phathuynh897@gmai.com", "Admin");
        // Cài đặt email người nhận
        helper.setTo(order.getOrderAddress().getEmail());

        msg = "<p>Chào [[name]],</p>"
                + "<p>Cảm ơn bạn đã đặt hàng! Chúng tôi rất vui thông báo rằng đơn hàng của bạn hiện tại đang ở trạng thái <b>[[orderStatus]]</b>.</p>"
                + "<p><b>Chi tiết sản phẩm:</b></p>"
                + "<p>Tên sản phẩm: [[productName]]</p>"
                + "<p>Danh mục: [[category]]</p>"
                + "<p>Số lượng: [[quantity]]</p>"
                + "<p>Giá: [[price]]</p>"
                + "<p>Hình thức thanh toán: [[paymentType]]</p>"
                + "<p>Chúng tôi sẽ tiếp tục xử lý đơn hàng của bạn và thông báo cho bạn khi có cập nhật mới. Cảm ơn bạn đã tin tưởng mua sắm cùng chúng tôi!</p>";


        msg=msg.replace("[[name]]",order.getOrderAddress().getFirstName());
        msg=msg.replace("[[orderStatus]]",status);
        msg=msg.replace("[[productName]]", order.getProduct().getTitle());
        msg=msg.replace("[[category]]", order.getProduct().getCategory());
        msg=msg.replace("[[quantity]]", order.getQuantity().toString());
        msg=msg.replace("[[price]]", order.getPrice().toString());
        msg=msg.replace("[[paymentType]]", order.getPaymentType());

        helper.setSubject("Trạng thái đơn hàng sản phẩm");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }

        public UserDtls getLoggedInUserDetails(Principal p) {
            // Lấy email của người dùng đã đăng nhập từ Principal (thường được cung cấp bởi Spring Security)
            String email = p.getName();

            // Dùng email để tìm thông tin người dùng từ dịch vụ UserService
            UserDtls userDtls = userService.getUserByEmail(email);

            // Trả về đối tượng UserDtls chứa thông tin chi tiết của người dùng
            return userDtls;
        }
}
