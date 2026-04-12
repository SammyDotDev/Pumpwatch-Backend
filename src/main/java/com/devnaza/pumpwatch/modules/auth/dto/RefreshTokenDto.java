package com.devnaza.pumpwatch.modules.auth.dto;

import jakarta.validation.constraints.NotNull;

public record RefreshTokenDto (@NotNull(message = "Refresh token cannot be " +
                                                          "null") String refreshToken){}
