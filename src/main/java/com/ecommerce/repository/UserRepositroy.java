package com.ecommerce.repository;

import com.ecommerce.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositroy extends JpaRepository<User, Long> {
  Optional<User> findByUserName(String userName);

  Optional<User> findByUserNameAndPassword(String userName, String password);

  Optional<User> findByAuthUserId(String authUserId);

  Optional<User> findByEmail(String authUserId);
}
