package com.devnaza.moniepointinspobackend.dto.requestsDto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserLogin(@NotBlank String username, @NotBlank String password) {
}
