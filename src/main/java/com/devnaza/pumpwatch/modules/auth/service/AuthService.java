package com.devnaza.pumpwatch.modules.auth.service;

import com.devnaza.pumpwatch.exception.InvalidPasswordException;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.dto.UserLogin;
import com.devnaza.pumpwatch.modules.user.model.User;

import java.util.Map;

public interface AuthService {

    UserDto createUser(User user);

    User loadUserByEmail(String email);

    Map<String, Object> validateUser(UserLogin userLogin) throws InvalidPasswordException;
}