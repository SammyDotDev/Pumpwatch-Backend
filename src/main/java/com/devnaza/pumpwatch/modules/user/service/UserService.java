package com.devnaza.pumpwatch.modules.user.service;

import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.dto.UserLogin;
import com.devnaza.pumpwatch.modules.user.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

public interface UserService {

    public UserDto getUser();
}
