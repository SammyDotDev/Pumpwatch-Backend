package com.devnaza.moniepointinspobackend.utils.helpers;

import com.devnaza.moniepointinspobackend.dto.UserDto;
import com.devnaza.moniepointinspobackend.model.User;

public class ToEntity {


    public ToEntity(){}

    public User convertToEntity(UserDto userDto){

        User user = new User();
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPhoneNumber(String.valueOf(userDto.phoneNumber()));
        user.setPassword(userDto.password());
        return user;
    }

}
