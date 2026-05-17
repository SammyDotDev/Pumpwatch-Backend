package com.devnaza.pumpwatch.modules.auth;

import com.devnaza.pumpwatch.dto.ApiResponseDto;
import com.devnaza.pumpwatch.modules.auth.dto.RefreshTokenDto;
import com.devnaza.pumpwatch.modules.auth.dto.TokenPair;
import com.devnaza.pumpwatch.modules.auth.service.impl.AuthServiceImpl;
import com.devnaza.pumpwatch.modules.auth.service.impl.RefreshTokenServiceImpl;
import com.devnaza.pumpwatch.modules.user.dto.UserDto;
import com.devnaza.pumpwatch.modules.user.dto.UserLogin;
import com.devnaza.pumpwatch.modules.user.model.User;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {


    private final AuthServiceImpl authServiceImpl;
    private final RefreshTokenServiceImpl refreshTokenServiceImpl;



    public AuthController(AuthServiceImpl authServiceImpl,
                          RefreshTokenServiceImpl refreshTokenServiceImpl) {
        this.authServiceImpl = authServiceImpl;
        this.refreshTokenServiceImpl = refreshTokenServiceImpl;
    }



    @PostMapping("/register")
    public ResponseEntity<ApiResponseDto> signup(@RequestBody @Valid User user) throws Exception {
        UserDto savedUser = authServiceImpl.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto("User created " + "successfully",
                savedUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDto> login(@Valid @RequestBody UserLogin userLogin){


        Map<String, Object> validatedUser =
                authServiceImpl.validateUser(userLogin);



        log.info("User login email: {}", userLogin.email());
        return ResponseEntity.ok().body(new ApiResponseDto("Sign in " +
                                                                   "successful", validatedUser));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPair> refresh(@Valid @RequestBody RefreshTokenDto refreshToken){
        TokenPair tokens =
                refreshTokenServiceImpl.rotateRefreshToken(refreshToken.refreshToken());
        return ResponseEntity.ok().body(tokens);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDto> logout(@RequestHeader("Authorization") String authHeader){
        return ResponseEntity.ok().body(ApiResponseDto.builder().message("Logged out successfully").data(authServiceImpl.logoutUser(authHeader)).build());
    }

}
