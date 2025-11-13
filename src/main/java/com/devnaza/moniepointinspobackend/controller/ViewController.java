package com.devnaza.moniepointinspobackend.controller;

import com.devnaza.moniepointinspobackend.model.User;
import com.devnaza.moniepointinspobackend.service.UserServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    public ViewController(UserServiceImpl userServiceImpl) {
    }

    @GetMapping("/signup")
    private String showSignupForm(Model model){
        model.addAttribute("user", new User());
        return "signup";
    }

    @GetMapping("/login")
    private String showLoginForm(Model model){
        model.addAttribute("user", new User());
        return "login";
    }
}
