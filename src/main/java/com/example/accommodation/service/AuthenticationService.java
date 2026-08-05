package com.example.accommodation.service;

import com.example.accommodation.dto.auth.UserLoginRequestDto;
import com.example.accommodation.dto.auth.UserLoginResponseDto;
import com.example.accommodation.dto.user.UserRegistrationRequestDto;
import com.example.accommodation.dto.user.UserResponseDto;

public interface AuthenticationService {

    UserResponseDto register(UserRegistrationRequestDto requestDto);

    UserLoginResponseDto login(UserLoginRequestDto requestDto);
}
