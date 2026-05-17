package com.devnaza.pumpwatch.modules.auth.service.impl;

import com.devnaza.pumpwatch.exception.EmailNotFoundException;
import com.devnaza.pumpwatch.exception.InvalidPasswordException;
import com.devnaza.pumpwatch.exception.UserAlreadyExistsException;
import com.devnaza.pumpwatch.modules.auth.model.RefreshToken;
import com.devnaza.pumpwatch.modules.auth.repository.RefreshTokenRepository;
import com.devnaza.pumpwatch.modules.auth.service.AuthService;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.dto.UserLogin;
import com.devnaza.pumpwatch.modules.user.model.User;
import com.devnaza.pumpwatch.modules.user.repository.UserRepository;
import com.devnaza.pumpwatch.utils.helpers.ToEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {


    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;
    private final JWTServiceImpl jwtServiceImpl;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenServiceImpl refreshTokenServiceImpl, JWTServiceImpl jwtServiceImpl, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
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
        return UserDto.builder().userId(savedUser.getUserId()).firstName(savedUser.getFirstName()).lastName(savedUser.getLastName()).email(savedUser.getEmail()).phoneNumber(savedUser.getPhoneNumber()).build();
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
        authResponse.put("user", UserDto.builder().userId(user.getUserId()).firstName(user.getFirstName()).lastName(user.getLastName()).email(user.getEmail()).phoneNumber(user.getPhoneNumber()).build());
        authResponse.put("access_token", accessToken);
        authResponse.put("refresh_token", refreshToken.getToken());

        return authResponse;
    }

    public String logoutUser(String authHeader){
        String token = null;
        if((authHeader != null) && (authHeader.startsWith("Bearer "))){
            token = authHeader.substring(7);
            log.info("Bearer token: {}", token);
        }
        Jws<Claims> accessToken = jwtServiceImpl.parseToken(token);
        refreshTokenRepository.deleteByUserId(UUID.fromString(accessToken.getPayload().getId()));
        log.info(accessToken.getPayload().getId());
        return "log out successful";
    }
}
