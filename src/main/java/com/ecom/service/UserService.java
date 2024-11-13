package com.ecom.service;

import com.ecom.model.UserDtls;

public interface UserService {


     // Phương thức để lưu thông tin người dùng vào cơ sở dữ liệu
    public UserDtls saveUser(UserDtls userDtls);
}
