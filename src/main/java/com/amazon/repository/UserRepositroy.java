package com.amazon.repository;

import com.amazon.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositroy extends JpaRepository<Users, Long> {
    Users findByUserName(String userName);

    Optional<Users> findByUserNameAndPassword(String userName, String password);
}
