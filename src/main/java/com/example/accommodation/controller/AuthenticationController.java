package com.example.accommodation.controller;

import com.example.accommodation.dto.auth.UserLoginRequestDto;
import com.example.accommodation.dto.auth.UserLoginResponseDto;
import com.example.accommodation.dto.user.UserRegistrationRequestDto;
import com.example.accommodation.dto.user.UserResponseDto;
import com.example.accommodation.service.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Authentication", description = "Endpoints for registration and login")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto register(@RequestBody @Valid UserRegistrationRequestDto requestDto) {
        return authenticationService.register(requestDto);
    }

    @PostMapping("/auth/login")
    public UserLoginResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.login(requestDto);
    }
}
