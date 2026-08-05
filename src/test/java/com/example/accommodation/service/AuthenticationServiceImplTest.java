package com.example.accommodation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.accommodation.dto.user.UserRegistrationRequestDto;
import com.example.accommodation.dto.user.UserResponseDto;
import com.example.accommodation.exception.RegistrationException;
import com.example.accommodation.mapper.UserMapper;
import com.example.accommodation.model.User;
import com.example.accommodation.repository.UserRepository;
import com.example.accommodation.security.JwtUtil;
import com.example.accommodation.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(
                userRepository, userMapper, passwordEncoder, authenticationManager, jwtUtil);
    }

    @Test
    void register_emailAlreadyExists_throwsException() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("taken@example.com");
        requestDto.setPassword("password123");
        requestDto.setFirstName("Jane");
        requestDto.setLastName("Doe");

        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authenticationService.register(requestDto))
                .isInstanceOf(RegistrationException.class);
    }

    @Test
    void register_newEmail_savesUser() {
        UserRegistrationRequestDto requestDto = new UserRegistrationRequestDto();
        requestDto.setEmail("new@example.com");
        requestDto.setPassword("password123");
        requestDto.setFirstName("Jane");
        requestDto.setLastName("Doe");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("new@example.com");

        UserResponseDto expectedDto = new UserResponseDto();
        expectedDto.setId(1L);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedDto);

        UserResponseDto actual = authenticationService.register(requestDto);

        assertThat(actual.getId()).isEqualTo(1L);
    }
}