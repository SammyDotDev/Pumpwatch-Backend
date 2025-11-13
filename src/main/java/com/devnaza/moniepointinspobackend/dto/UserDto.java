package com.devnaza.moniepointinspobackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.UUID;

@Builder
public record UserDto(@NotBlank(message = "First must not be null") String firstName, @NotBlank(message = "Last Name must not be null") String lastName, @NotBlank(message = "Username must not be null") String username, @NotBlank(message = "Email must not be null") @Email String email, @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) @NotBlank(message = "Password must not be null") String password, @NotBlank(message = "Phone number must not be null") String phoneNumber) {
}
