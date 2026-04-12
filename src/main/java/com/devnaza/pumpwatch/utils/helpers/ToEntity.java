package com.devnaza.pumpwatch.utils.helpers;

import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.model.User;

public class ToEntity {


    public ToEntity(){}

    public User convertToEntity(UserDto userDto){

        User user = new User();
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPhoneNumber(String.valueOf(userDto.phoneNumber()));
        return user;
    }

}
