package com.example.accommodation.dto.user;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName
) {
}
