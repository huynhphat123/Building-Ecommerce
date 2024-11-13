package com.ecom.config;

import com.ecom.model.UserDtls;
import com.ecom.repository.UserReponsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserReponsitory userReponsitory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDtls user = userReponsitory.findByEmail(username);

        if(user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new CustomUser(user);
    }
}
