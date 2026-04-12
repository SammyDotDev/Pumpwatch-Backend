package com.devnaza.pumpwatch.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserDto(@NotBlank(message = "First name must not be null") String firstName, @NotBlank(message = "Last Name must not be null") String lastName, @NotBlank(message = "Username must not be null") String username, @NotBlank(message = "Email must not be null") @Email String email,  @NotBlank(message = "Phone number must not be null") String phoneNumber) {
}
