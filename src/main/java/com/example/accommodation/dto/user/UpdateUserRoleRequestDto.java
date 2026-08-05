package com.example.accommodation.dto.user;

import com.example.accommodation.model.enums.Role;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRoleRequestDto {
    @NotNull
    private Role role;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
