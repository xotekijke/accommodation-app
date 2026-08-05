package com.example.accommodation.dto.auth;

public class UserLoginResponseDto {
    private String token;

    public UserLoginResponseDto() {
    }

    public UserLoginResponseDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
