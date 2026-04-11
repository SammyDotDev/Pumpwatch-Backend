package com.devnaza.pumpwatch.modules.user.service;

import com.devnaza.pumpwatch.exception.EmailNotFoundException;
import com.devnaza.pumpwatch.exception.InvalidPasswordException;
import com.devnaza.pumpwatch.exception.UserAlreadyExistsException;
import com.devnaza.pumpwatch.modules.auth.service.JWTService;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.dto.UserLogin;
import com.devnaza.pumpwatch.modules.user.model.User;
import com.devnaza.pumpwatch.modules.user.repository.UserRepository;
import com.devnaza.pumpwatch.utils.helpers.ToDto;
import com.devnaza.pumpwatch.utils.helpers.ToEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private JWTService jwtService;


    public UserServiceImpl(UserRepository userRepository, ToEntity toEntity,
                           ToDto toDto, PasswordEncoder passwordEncoder) {
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

        if(userRepository.existsByEmail(userDto.email())){
            throw new UserAlreadyExistsException("User with email " + userDto.email() + " already exists");
        }
        User savedUser = userRepository.save(user);


        log.info("User created: {}", savedUser.getId());
        return toDto.convertToUserDto(savedUser);
    }


    public UserDto loadUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found with username: {}", email);
            throw new EmailNotFoundException("User with email: " + email +
                                                         " " +
                                                         "not " +
                                                         "found");
        });

//        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_USER").accountLocked(false).build();

        return toDto.convertToUserDto(user);
    }

    public String validateUser(UserLogin userLogin) throws InvalidPasswordException {
        UserDto user = this.loadUserByEmail(userLogin.username());

        if(!passwordEncoder.matches(userLogin.password(), user.password())){
            throw new InvalidPasswordException("Invalid password for user: " + user.username());
        }

        return jwtService.generateAccessToken(toEntity.convertToEntity(user));
    }

    @Override
    public String getUser() {
        return "";
    }




}
