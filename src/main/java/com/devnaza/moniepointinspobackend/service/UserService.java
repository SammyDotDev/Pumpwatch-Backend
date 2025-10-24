package com.devnaza.moniepointinspobackend.service;

import com.devnaza.moniepointinspobackend.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    public UserDto createUser(UserDto userDto);
}
