package com.devnaza.pumpwatch.modules.auth.service.impl;

import com.devnaza.pumpwatch.modules.auth.service.JWTService;
import com.devnaza.pumpwatch.modules.user.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JWTServiceImpl implements JWTService {
    private final SecretKey secretKey;
    private final Duration accessExpiry;
    private final Duration refreshExpiry;

    public JWTServiceImpl(@Value("${security.jwt.secret}") String secret, @Value("${security.jwt.access-token-expiration-minutes}") long accessMins,
                          @Value("${security.jwt.refresh-token-expiration-days}") long refreshDays){
            this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiry = Duration.ofMinutes(accessMins);;
        this.refreshExpiry =  Duration.ofDays(refreshDays);
    }

    public String generateAccessToken(User user){
        return getClaims(user.getEmail(), accessExpiry);
    }

    public String generateRefreshToken(User user){
        return getClaims(user.getEmail(), refreshExpiry);
    }

    public Jws<Claims> parseToken(String token){
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
    }

    public boolean validateToken(String token){
        try{
            parseToken(token);
            return true;
        }catch (JwtException | IllegalArgumentException e){
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }
    private String getClaims(String email, Duration refreshExpiry) {
        Map<String,Object> claims = new HashMap<>();
        Instant now = Instant.now();

        return Jwts.builder().claims().add(claims).subject(email).issuedAt(Date.from(now)).expiration(Date.from(now.plus(refreshExpiry))).and().signWith(secretKey).compact();
    }
}
