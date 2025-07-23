package com.ecommerce.utils;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepositroy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidationUtils {

  private final UserRepositroy userRepositroy;

  public User validateUser(String authUserId) {
    return userRepositroy
        .findByAuthUserId(authUserId)
        .orElseThrow(() -> new RuntimeException("User not found with authUserId: " + authUserId));
  }
}
