package com.example.accommodation.service.impl;

import com.example.accommodation.dto.auth.UserLoginRequestDto;
import com.example.accommodation.dto.auth.UserLoginResponseDto;
import com.example.accommodation.dto.user.UserRegistrationRequestDto;
import com.example.accommodation.dto.user.UserResponseDto;
import com.example.accommodation.exception.RegistrationException;
import com.example.accommodation.mapper.UserMapper;
import com.example.accommodation.model.User;
import com.example.accommodation.model.enums.Role;
import com.example.accommodation.repository.UserRepository;
import com.example.accommodation.security.JwtUtil;
import com.example.accommodation.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new RegistrationException(
                    "User with email " + requestDto.email() + " already exists");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.password()));
        user.setRole(Role.CUSTOMER);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.email(), requestDto.password()));
        String token = jwtUtil.generateToken(requestDto.email());
        return new UserLoginResponseDto(token);
    }
}
