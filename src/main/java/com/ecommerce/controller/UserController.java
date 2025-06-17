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
@RequestMapping(path = "/amazon/user")
public class UserController {

  @Autowired UserService userService;

  @PostMapping("/new/signup")
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewUser(@RequestBody UserDto.UserSignUpDto user) {
    userService.createNewUser(user);
  }

  @GetMapping("/fetch")
  @ResponseBody
  public ResponseEntity<?> getUser(@RequestBody UserDto.UserFetch user) {
    return userService.getUser(user);
  }

  @GetMapping("/fetch/all")
  @ResponseBody
  public ResponseEntity<List<UserResponseDto.UserDetailsResponseDto>> getAllUsers() {
    return userService.getAllUsers();
  }
}
