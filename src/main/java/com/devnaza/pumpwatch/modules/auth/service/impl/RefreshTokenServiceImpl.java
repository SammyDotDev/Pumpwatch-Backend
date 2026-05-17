package com.devnaza.pumpwatch.modules.auth.service.impl;

import com.devnaza.pumpwatch.exception.InvalidTokenException;
import com.devnaza.pumpwatch.exception.UserAlreadyLoggedInException;
import com.devnaza.pumpwatch.modules.auth.dto.TokenPair;
import com.devnaza.pumpwatch.modules.auth.model.RefreshToken;
import com.devnaza.pumpwatch.modules.auth.repository.RefreshTokenRepository;
import com.devnaza.pumpwatch.modules.auth.service.RefreshTokenService;
import com.devnaza.pumpwatch.modules.user.model.User;
import com.devnaza.pumpwatch.modules.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.Instant;

@Slf4j
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private UserRepository userRepository;
    private final JWTServiceImpl jwtServiceImpl;

    @Value("${security.jwt.refresh-token-expiration-days}")
    private int expirationDays;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                                   UserRepository userRepository, JWTServiceImpl jwtServiceImpl) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.jwtServiceImpl = jwtServiceImpl;
    }

    //    create new refreshToken

    @Override
    public RefreshToken createRefreshToken(User user){
//        create token
        String refreshToken = jwtServiceImpl.generateRefreshToken(user);
        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);


//        populate refresh_token entity
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(hashToken(refreshToken));
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiresAt(Instant.now().plusSeconds(expirationDays* 86400L));

        log.info("refreshToken: {}",
                refreshTokenEntity.getToken());
        log.info("expiry duration: {}", expirationDays* 86400L);

//        save token to db
        refreshTokenRepository.save(refreshTokenEntity);
        return refreshTokenEntity;
    }

    public TokenPair rotateRefreshToken(String rawToken){
//        checking if the token is in db
        RefreshToken storedRefreshToken =
                refreshTokenRepository.findByToken(rawToken).orElseThrow(()->new InvalidTokenException("Refresh Token not valid. Please log in again"));

//        check if is revoked
        if(storedRefreshToken.isRevoked()){
            refreshTokenRepository.deleteAllByUser(storedRefreshToken.getUser());
            throw new InvalidTokenException("Suspicious activity detected. Please log in again.");
        }

//        check expiry
        if(storedRefreshToken.getExpiresAt().isBefore(Instant.now())){
//        delete old token
            refreshTokenRepository.delete(storedRefreshToken);
        }

        String accessToken =
                jwtServiceImpl.generateAccessToken(storedRefreshToken.getUser());

        String refreshToken =
                hashToken(jwtServiceImpl.generateRefreshToken(storedRefreshToken.getUser()));

//        create new access token

//        create new refresh token
        return new TokenPair(accessToken, refreshToken);
    }

    private String hashToken(String rawToken) {
        // SHA-256 hash — fast and one-way
        return DigestUtils.sha256Hex(rawToken);
    }
}
