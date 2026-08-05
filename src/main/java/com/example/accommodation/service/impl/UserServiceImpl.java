package com.example.accommodation.service.impl;

import com.example.accommodation.dto.user.UpdateUserRoleRequestDto;
import com.example.accommodation.dto.user.UserResponseDto;
import com.example.accommodation.dto.user.UserUpdateRequestDto;
import com.example.accommodation.exception.EntityNotFoundException;
import com.example.accommodation.mapper.UserMapper;
import com.example.accommodation.model.User;
import com.example.accommodation.repository.UserRepository;
import com.example.accommodation.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponseDto getProfile(User user) {
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateProfile(User user, UserUpdateRequestDto requestDto) {
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserResponseDto updateRole(Long userId, UpdateUserRoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find user by id " + userId));
        user.setRole(requestDto.getRole());
        return userMapper.toDto(userRepository.save(user));
    }
}
