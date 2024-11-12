package com.ecom.service;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserReponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserReponsitory userReponsitory;
    
    @Override
    public UserDtls saveUser(UserDtls userDtls) {
        UserDtls saveUser = userReponsitory.save(userDtls);
        return saveUser;
    }
}
