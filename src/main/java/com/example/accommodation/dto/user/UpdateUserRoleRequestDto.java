package com.example.accommodation.dto.user;

import com.example.accommodation.model.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequestDto(@NotNull Role role) {
}
