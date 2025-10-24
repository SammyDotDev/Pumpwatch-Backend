package com.devnaza.moniepointinspobackend.utils.helpers;

import com.devnaza.moniepointinspobackend.dto.UserDto;
import com.devnaza.moniepointinspobackend.model.User;
import org.springframework.context.annotation.Bean;

public class ToDto {

    public ToDto(){}

    public UserDto convertToUserDto(User user){
        return new UserDto(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(), user.getPassword(), user.getPhoneNumber());
    }

}
