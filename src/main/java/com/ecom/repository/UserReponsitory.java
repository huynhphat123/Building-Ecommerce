package com.ecom.repository;

import com.ecom.model.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;

// Giao diện JpaRepository cung cấp sẵn các phương thức CRUD cho entity UserDtls
public interface UserReponsitory extends JpaRepository<UserDtls, Integer> {

    public UserDtls findByEmail(String email);
}
