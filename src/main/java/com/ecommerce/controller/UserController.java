package com.ecommerce.controller;

import com.ecommerce.requestdto.UserDto;
import com.ecommerce.responsedto.UserResponseDto;
import com.ecommerce.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/ecommerce/users")
public class UserController {

  @Autowired UserService userService;

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
