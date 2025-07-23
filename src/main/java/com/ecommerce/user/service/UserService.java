package com.ecommerce.user.service;

import com.ecommerce.user.dto.UserDto;
import com.ecommerce.user.dto.UserResponseDto;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepositroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired UserRepositroy userRepository;

  public void createNewUser(UserDto.UserSignUpDto user) {

    var existingUser =
        userRepository
            .findByUserName(user.userName())
            .orElseThrow(() -> new RuntimeException("User not found"));
    if (existingUser != null) {
      throw new RuntimeException("User with given username already exists");
    }

    try {
      User newUser = new User();
      newUser.setUserFirstName(user.userFirstName());
      newUser.setUserLastName(user.userLastName());
      newUser.setUserName(user.userName());
      newUser.setPassword(user.password());
      newUser.setMobileNumber(user.mobileNumber());
      userRepository.save(newUser);
    } catch (DataAccessException ex) {
      throw new RuntimeException("Failed to create a new user", ex);
    }
  }

  public ResponseEntity<?> getUser(UserDto.UserFetch user) {
    Optional<User> u = userRepository.findByUserNameAndPassword(user.userName(), user.password());

    try {
      if (u.isPresent()) {
        UserResponseDto.UserDetailsResponseDto userDetailsResponseDto =
            UserResponseDto.UserDetailsResponseDto.builder()
                .id(u.get().getId())
                .userFirstName(u.get().getUserFirstName())
                .userLastName(u.get().getUserLastName())
                .userName(u.get().getUserName())
                .mobileNumber(u.get().getMobileNumber())
                .build();

        return new ResponseEntity<>(userDetailsResponseDto, HttpStatus.OK);
      } else {
        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<>("Data not found", HttpStatus.NO_CONTENT);
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
