package com.devnaza.pumpwatch.modules.auth.service.impl;

import com.devnaza.pumpwatch.exception.EmailNotFoundException;
import com.devnaza.pumpwatch.exception.InvalidPasswordException;
import com.devnaza.pumpwatch.exception.UserAlreadyExistsException;
import com.devnaza.pumpwatch.modules.auth.model.RefreshToken;
import com.devnaza.pumpwatch.modules.auth.service.AuthService;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.dto.UserLogin;
import com.devnaza.pumpwatch.modules.user.model.User;
import com.devnaza.pumpwatch.modules.user.repository.UserRepository;
import com.devnaza.pumpwatch.utils.helpers.ToDto;
import com.devnaza.pumpwatch.utils.helpers.ToEntity;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ToEntity toEntity;
    private final ToDto toDto;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final JWTServiceImpl jwtServiceImpl;

    public AuthServiceImpl(UserRepository userRepository, ToEntity toEntity, ToDto toDto, PasswordEncoder passwordEncoder, RefreshTokenServiceImpl refreshTokenServiceImpl, JWTServiceImpl jwtServiceImpl) {
        this.userRepository = userRepository;
        this.toEntity = toEntity;
        this.toDto = toDto;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
        this.jwtServiceImpl = jwtServiceImpl;
    }

    @Override
    public UserDto createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " already exists");
        }
        User savedUser = userRepository.save(user);


        log.info("User created: {}", savedUser.getId());
        return toDto.convertToUserDto(savedUser);
    }


    public User loadUserByEmail(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            log.error("User not found with username: {}", email);
            throw new EmailNotFoundException("User with email: " + email +
                                                     " " +
                                                     "not " +
                                                     "found");
        });

//        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).authorities("ROLE_USER").accountLocked(false).build();

        return user;
    }

    public Map<String, Object> validateUser(UserLogin userLogin) throws InvalidPasswordException {
        User user = this.loadUserByEmail(userLogin.email());

        if(!passwordEncoder.matches(userLogin.password(), user.getPassword())){
            throw new InvalidPasswordException("Invalid password for user: " + user.getEmail());
        }


        String accessToken =
                jwtServiceImpl.generateAccessToken(user);

        RefreshToken refreshToken =
                refreshTokenServiceImpl.createRefreshToken(user);

        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("user", user);
        authResponse.put("access_token", accessToken);
        authResponse.put("refresh_token", refreshToken.getToken());

        return authResponse;
    }
}
