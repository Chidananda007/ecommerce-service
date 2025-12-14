package com.ecommerce.controller;

import com.ecommerce.requestdto.UserDto;
import com.ecommerce.responsedto.UserResponseDto;
import com.ecommerce.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/ecommerce/users")
public class UserController {

  private final UserService userService;

  @PostMapping("/signup")
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewUser(@RequestBody UserDto.UserSignUpRequest user) {
    userService.createNewUser(user);
  }

  @PostMapping("/login")
  @ResponseBody
  public ResponseEntity<?> getUser(@RequestBody UserDto.UserLoginRequest user) {
    return userService.getUser(user);
  }

  @GetMapping
  @ResponseBody
  public ResponseEntity<List<UserResponseDto.UserDetailsResponseDto>> getAllUsers() {
    return userService.getAllUsers();
  }
}
