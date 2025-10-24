package com.devnaza.moniepointinspobackend.controller;

import com.devnaza.moniepointinspobackend.dto.UserDto;
import com.devnaza.moniepointinspobackend.dto.apiResponse.ApiResponseDto;
import com.devnaza.moniepointinspobackend.model.User;
import com.devnaza.moniepointinspobackend.repository.UserRepository;
import com.devnaza.moniepointinspobackend.service.UserServiceImpl;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
//@RequestMapping("/api/v1/auth")
public class AuthController {


    private final UserServiceImpl userServiceImpl;

    @Autowired
    UserRepository userRepository;

    public AuthController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }



    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String signup(@ModelAttribute @RequestBody @Valid UserDto userDto,
                    BindingResult bindingResult, Model model){
        if(userRepository.existsByEmail(userDto.email())){
            bindingResult.rejectValue("email", "error.user", "email already exists");
            return null;
        }

        if(bindingResult.hasErrors()){
            log.info("Binding result errors: {}", bindingResult.getAllErrors());
            model.addAttribute("user", new User());
            return "signup";
        }
        UserDto savedUser = userServiceImpl.createUser(userDto);
//        return new ResponseEntity<ApiResponseDto>(new ApiResponseDto("User created successfully", savedUser), HttpStatus.CREATED);
        return "redirect:/login?success";
    }


}
