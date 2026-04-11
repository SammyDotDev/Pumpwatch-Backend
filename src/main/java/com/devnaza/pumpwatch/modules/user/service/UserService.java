package com.devnaza.pumpwatch.modules.user.service;

import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.dto.UserLogin;

public interface UserService {

    public UserDto createUser(UserDto userDto);

    public String validateUser(UserLogin userLogin);

    public String getUser();
}
