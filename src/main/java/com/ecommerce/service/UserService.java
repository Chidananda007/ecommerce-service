package com.ecommerce.service;

import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepositroy;
import com.ecommerce.requestdto.UserDto;
import com.ecommerce.responsedto.UserResponseDto;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepositroy userRepository;

  private final PasswordEncoder passwordEncoder;

  public void createNewUser(UserDto.UserSignUpRequest request) {

    var userByUserName = userRepository.findByUserName(request.userName());
    var userByEmail = userRepository.findByEmail(request.userName());
    if (userByUserName.isPresent() || userByEmail.isPresent()) {
      throw new RuntimeException("User with given username or email already exists");
    }

    try {
      userRepository.save(buildUser(request, new User()));
    } catch (DataAccessException ex) {
      throw new RuntimeException(
          "Failed to create a new user : %s ".formatted(ex.getMessage()), ex);
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
    return user;
  }

  public ResponseEntity<?> getUser(UserDto.UserFetch request) {
    Optional<User> optionalUser =
        userRepository.findByUserNameAndPassword(request.userName(), request.password());

    try {
      optionalUser
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
    throw new RuntimeException("Failed to fetch user");
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
