package com.devnaza.moniepointinspobackend.service;

import com.devnaza.moniepointinspobackend.dto.UserDto;
import com.devnaza.moniepointinspobackend.dto.requestsDto.UserLogin;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    public UserDto createUser(UserDto userDto);

    public UserDto validateUser(UserLogin userLogin);
}
