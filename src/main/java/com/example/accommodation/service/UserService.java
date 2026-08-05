package com.example.accommodation.service;

import com.example.accommodation.dto.user.UpdateUserRoleRequestDto;
import com.example.accommodation.dto.user.UserResponseDto;
import com.example.accommodation.dto.user.UserUpdateRequestDto;
import com.example.accommodation.model.User;

public interface UserService {

    UserResponseDto getProfile(User user);

    UserResponseDto updateProfile(User user, UserUpdateRequestDto requestDto);

    UserResponseDto updateRole(Long userId, UpdateUserRoleRequestDto requestDto);
}
