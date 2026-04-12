package com.devnaza.pumpwatch.modules.auth.dto;

import jakarta.validation.constraints.NotNull;

public record TokenPair(@NotNull String accessToken,
                        @NotNull String refreshToken) {
}
