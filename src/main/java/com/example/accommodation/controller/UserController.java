package com.example.accommodation.controller;

import com.example.accommodation.dto.user.UpdateUserRoleRequestDto;
import com.example.accommodation.dto.user.UserResponseDto;
import com.example.accommodation.dto.user.UserUpdateRequestDto;
import com.example.accommodation.model.User;
import com.example.accommodation.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "Endpoints for managing user profiles and roles")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('MANAGER')")
    public UserResponseDto updateRole(@PathVariable Long id,
            @RequestBody @Valid UpdateUserRoleRequestDto requestDto) {
        return userService.updateRole(id, requestDto);
    }

    @GetMapping("/me")
    public UserResponseDto getProfile(@AuthenticationPrincipal User user) {
        return userService.getProfile(user);
    }

    @PutMapping("/me")
    public UserResponseDto updateProfile(@AuthenticationPrincipal User user,
            @RequestBody @Valid UserUpdateRequestDto requestDto) {
        return userService.updateProfile(user, requestDto);
    }

    @PatchMapping("/me")
    public UserResponseDto patchProfile(@AuthenticationPrincipal User user,
            @RequestBody @Valid UserUpdateRequestDto requestDto) {
        return userService.updateProfile(user, requestDto);
    }
}
