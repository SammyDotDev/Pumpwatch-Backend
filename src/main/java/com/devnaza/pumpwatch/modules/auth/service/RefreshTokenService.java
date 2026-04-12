package com.devnaza.pumpwatch.modules.auth.service;

import com.devnaza.pumpwatch.modules.auth.dto.TokenPair;
import com.devnaza.pumpwatch.modules.auth.model.RefreshToken;
import com.devnaza.pumpwatch.modules.user.model.User;

public interface RefreshTokenService {

    public RefreshToken createRefreshToken(User user);

    public TokenPair rotateRefreshToken(String refreshToken);
}
