package com.example.accommodation.service.impl;

import com.example.accommodation.dto.user.UpdateUserRoleRequestDto;
import com.example.accommodation.dto.user.UserResponseDto;
import com.example.accommodation.dto.user.UserUpdateRequestDto;
import com.example.accommodation.exception.EntityNotFoundException;
import com.example.accommodation.mapper.UserMapper;
import com.example.accommodation.model.User;
import com.example.accommodation.repository.UserRepository;
import com.example.accommodation.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponseDto getProfile(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateProfile(User user, UserUpdateRequestDto requestDto) {
        userMapper.updateModel(requestDto, user);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateRole(Long userId, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user by id " + userId));
        user.setRole(requestDto.role());
        return userMapper.toDto(userRepository.save(user));
    }
}
