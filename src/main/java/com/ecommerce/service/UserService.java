package com.ecommerce.service;

import com.ecommerce.entity.RoleTemplate;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepositroy;
import com.ecommerce.requestdto.UserDto;
import com.ecommerce.responsedto.UserResponseDto;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepositroy userRepository;

  private final PasswordEncoder passwordEncoder;

  public void createNewUser(UserDto.UserSignUpRequest request) {
    log.info("Creating new user with username: {}", request.userName());

    var userByUserName = userRepository.findByUserName(request.userName());
    var userByEmail = userRepository.findByEmail(request.userName());
    if (userByUserName.isPresent() || userByEmail.isPresent()) {
      log.error("User with given username or email already exists: {}", request.userName());
      throw new RuntimeException("User with given username or email already exists");
    }

    try {
      userRepository.save(buildUser(request, new User()));
    } catch (Exception e) {
      throw new RuntimeException("Something went wrong : %s ".formatted(e.getMessage()), e);
    }
  }

  public User buildUser(UserDto.UserSignUpRequest request, User user) {
    user.setUserFirstName(request.userFirstName());
    user.setUserLastName(request.userLastName());
    user.setUserName(request.userName());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setEmail(request.email());
    user.setAuthUserId(UUID.randomUUID().toString());
    user.setMobileNumber(request.mobileNumber());
    user.setRoleTemplate(RoleTemplate.CUSTOMER);
    return user;
  }

  public ResponseEntity<?> getUser(UserDto.UserLoginRequest request) {
    Optional<User> optionalUser = userRepository.findByUserName(request.userName());

    try {
      return optionalUser
          .map(
              user -> {
                if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                  throw new RuntimeException("Invalid password");
                }
                UserResponseDto.UserDetailsResponseDto userDetailsResponseDto =
                    UserResponseDto.UserDetailsResponseDto.builder()
                        .id(user.getId())
                        .userFirstName(user.getUserFirstName())
                        .userLastName(user.getUserLastName())
                        .userName(user.getUserName())
                        .mobileNumber(user.getMobileNumber())
                        .build();
                return new ResponseEntity<>(userDetailsResponseDto, HttpStatus.OK);
              })
          .orElseThrow(() -> new RuntimeException("User not found"));
    } catch (Exception e) {
      throw new RuntimeException("Failed to fetch user : %s ".formatted(e.getMessage()), e);
    }
  }

  public ResponseEntity<List<UserResponseDto.UserDetailsResponseDto>> getAllUsers() {
    List<User> allUsers = userRepository.findAll();
    List<UserResponseDto.UserDetailsResponseDto> userDetailsList = new ArrayList<>();

    for (User user : allUsers) {
      UserResponseDto.UserDetailsResponseDto userDetailsResponseDto =
          UserResponseDto.UserDetailsResponseDto.builder()
              .id(user.getId())
              .userFirstName(user.getUserFirstName())
              .userLastName(user.getUserLastName())
              .userName(user.getUserName())
              .mobileNumber(user.getMobileNumber())
              .build();
      userDetailsList.add(userDetailsResponseDto);
    }

    if (!userDetailsList.isEmpty()) {
      return new ResponseEntity<>(userDetailsList, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }
}
