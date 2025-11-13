package com.devnaza.moniepointinspobackend.controller;

import com.devnaza.moniepointinspobackend.dto.UserDto;
import com.devnaza.moniepointinspobackend.dto.apiResponse.ApiResponseDto;
import com.devnaza.moniepointinspobackend.dto.requestsDto.UserLogin;
import com.devnaza.moniepointinspobackend.exception.ApiExceptionHandler;
import com.devnaza.moniepointinspobackend.exception.UserAlreadyExistsException;
import com.devnaza.moniepointinspobackend.repository.UserRepository;
import com.devnaza.moniepointinspobackend.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {


    private final UserServiceImpl userServiceImpl;
    private final UserRepository userRepository;

    public AuthController(UserServiceImpl userServiceImpl,
                          UserRepository userRepository) {
        this.userServiceImpl = userServiceImpl;
        this.userRepository = userRepository;
    }



    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponseDto> signup(@RequestBody @Valid UserDto userDto) throws Exception {
        if(userRepository.existsByEmail(userDto.email())){
            throw new UserAlreadyExistsException("User with email " + userDto.email() + " already exists");
        }

        UserDto savedUser = userServiceImpl.createUser(userDto);
        return new ResponseEntity<>(new ApiResponseDto("User created successfully", savedUser), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@Valid @RequestBody UserLogin userLogin){


        UserDto loadedUser =
                userServiceImpl.validateUser(userLogin);


        log.info("User login fields: {}", userLogin.username());
        return ResponseEntity.ok().body(new ApiResponseDto("Sign in " +
                                                                   "successful", loadedUser));
    }




}
