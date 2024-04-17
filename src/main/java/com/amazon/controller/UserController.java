package com.amazon.controller;

import com.amazon.entity.Users;
import com.amazon.requestdto.UserDto;
import com.amazon.responsedto.UserResponseDto;
import com.amazon.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping(path = "/amazon/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/new/signup")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public void createNewuser(@RequestBody UserDto.UserSignUpDto user)
    {
        userService.createNewUser(user);
    }

    @GetMapping("/fetch")
    @ResponseBody
    public ResponseEntity<?> getUser(@RequestBody UserDto.UserFetch user)
    {
       return  userService.getUser(user);
    }

    @GetMapping("/fetch/all")
    @ResponseBody
    public ResponseEntity<List<UserResponseDto.UserDetailsResponseDto>> getAllUsers()
    {
        return userService.getAllUsers();
    }

}
