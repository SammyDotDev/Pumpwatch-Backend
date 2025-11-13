package com.devnaza.moniepointinspobackend.service;

import com.devnaza.moniepointinspobackend.dto.UserDto;
import com.devnaza.moniepointinspobackend.dto.requestsDto.UserLogin;
import com.devnaza.moniepointinspobackend.exception.InvalidPasswordException;
import com.devnaza.moniepointinspobackend.model.User;
import com.devnaza.moniepointinspobackend.repository.UserRepository;
import com.devnaza.moniepointinspobackend.utils.helpers.ToDto;
import com.devnaza.moniepointinspobackend.utils.helpers.ToEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ToEntity toEntity;
    private final ToDto toDto;
    private final PasswordEncoder passwordEncoder;



    public UserServiceImpl(UserRepository userRepository, ToEntity toEntity,
                           ToDto toDto,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.toEntity = toEntity;
        this.toDto = toDto;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public UserDto createUser(UserDto userDto) {
        User user = toEntity.convertToEntity(userDto);
        String encodedPassword = passwordEncoder.encode(userDto.password());
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);


        log.info("User created: {}", savedUser.getId());
        return toDto.convertToUserDto(savedUser);
    }


    public UserDto loadUserByUsername(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User not found with username: {}", username);
            return new UsernameNotFoundException("User with username: " + username + " not found");
        });




//        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_USER").accountLocked(false).build();

        return toDto.convertToUserDto(user);
    }

    public UserDto validateUser(UserLogin userLogin) throws InvalidPasswordException{
        UserDto user = this.loadUserByUsername(userLogin.username());

        if(!passwordEncoder.matches(userLogin.password(), user.password())){
            throw new InvalidPasswordException("Invalid password for user: " + user.username());
        }

        return user;
    }
}
