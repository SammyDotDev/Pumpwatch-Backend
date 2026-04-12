package com.devnaza.pumpwatch.modules.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserLogin(@NotBlank String email, @NotBlank String password) {
}
