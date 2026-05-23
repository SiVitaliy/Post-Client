package com.example.postclientservice.dto.WithDto;

import com.example.postclientservice.dto.Dto.JwtResponseDto;
import com.example.postclientservice.dto.Dto.UserDto;

public record UserWithTokenDto(UserDto userDto, JwtResponseDto jwt) {
}
