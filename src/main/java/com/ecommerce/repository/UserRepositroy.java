package com.ecommerce.repository;

import com.ecommerce.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositroy extends JpaRepository<Users, Long> {
  Users findByUserName(String userName);

  Optional<Users> findByUserNameAndPassword(String userName, String password);
}
