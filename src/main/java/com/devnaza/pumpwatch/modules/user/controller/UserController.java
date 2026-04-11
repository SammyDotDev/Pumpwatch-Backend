package com.devnaza.pumpwatch.modules.user.controller;

import com.devnaza.pumpwatch.dto.ApiResponseDto;
import com.devnaza.pumpwatch.exception.UserAlreadyExistsException;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.dto.UserLogin;
import com.devnaza.pumpwatch.modules.user.repository.UserRepository;
import com.devnaza.pumpwatch.modules.user.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/user")
@RestController
public class UserController {
    private UserServiceImpl userService;
    private UserRepository userRepository;


    public UserController(UserServiceImpl userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

//    @GetMapping("/me")
//    public ResponseEntity<UserDto> getCurrentUser(){
//
//    }
}
