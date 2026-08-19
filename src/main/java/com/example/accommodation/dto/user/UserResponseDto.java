package com.example.accommodation.dto.user;

import com.example.accommodation.model.enums.Role;

public record UserResponseDto(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role role
) {
}
