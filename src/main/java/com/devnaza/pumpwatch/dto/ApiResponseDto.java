package com.devnaza.pumpwatch.dto;

import lombok.Builder;

@Builder
public record ApiResponseDto(String message, Object data) {
}
