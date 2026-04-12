package com.devnaza.pumpwatch.utils.helpers;

import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.model.User;

public class ToDto {

    public ToDto(){}

    public UserDto convertToUserDto(User user){
        return new UserDto(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPhoneNumber());
    }

}
