package com.ecom.service;

import com.ecom.model.UserDtls;

import java.util.List;

public interface UserService {

     // Phương thức để lưu thông tin người dùng vào cơ sở dữ liệu
    public UserDtls saveUser(UserDtls userDtls);

    public UserDtls getUserByEmail(String email);

    public List<UserDtls> getUsers(String role);

    Boolean updateAccountStatus(Integer id, Boolean status);
}
