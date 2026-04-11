package com.devnaza.pumpwatch.modules.auth.controller;

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
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {


    private final UserServiceImpl userServiceImpl;


    public AuthController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }



    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> signup(@RequestBody @Valid UserDto userDto) throws Exception {
        UserDto savedUser = userServiceImpl.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto("User created " + "successfully",
                savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@Valid @RequestBody UserLogin userLogin){


        Map<String, String> accessToken = new HashMap<>();
        String loadedUser =
                userServiceImpl.validateUser(userLogin);
        accessToken.put("accessToken", loadedUser);


        log.info("User login fields: {}", userLogin.username());
        return ResponseEntity.ok().body(new ApiResponseDto("Sign in " +
                                                                   "successful", accessToken));
    }




}
