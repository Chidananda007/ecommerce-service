package com.ecommerce.user.controller;

import com.ecommerce.user.dto.UserDto;
import com.ecommerce.user.dto.UserResponseDto;
import com.ecommerce.user.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/public/ecommerce")
public class UserController {

  @Autowired UserService userService;

  @PostMapping("/registration")
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewUser(@RequestBody UserDto.UserSignUpDto user) {
    userService.createNewUser(user);
  }

  @GetMapping("/login")
  @ResponseBody
  public ResponseEntity<?> getUser(@RequestBody UserDto.UserFetch user) {
    return userService.getUser(user);
  }

  @GetMapping()
  @ResponseBody
  public ResponseEntity<List<UserResponseDto.UserDetailsResponseDto>> getAllUsers() {
    return userService.getAllUsers();
  }
}
