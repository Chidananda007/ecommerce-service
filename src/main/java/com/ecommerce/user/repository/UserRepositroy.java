package com.ecommerce.user.repository;

import com.ecommerce.user.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepositroy extends JpaRepository<User, Long> {
  Optional<org.apache.catalina.User> findByUserName(String userName);

  Optional<User> findByUserNameAndPassword(String userName, String password);
}
