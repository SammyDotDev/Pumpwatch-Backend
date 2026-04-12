package com.devnaza.pumpwatch.modules.auth.service;

import com.devnaza.pumpwatch.modules.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

public interface JWTService {

    String generateAccessToken(User user);

    String generateRefreshToken(User user);

    Jws<Claims> parseToken(String token);

    boolean validateToken(String token);
}