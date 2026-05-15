package com.example.postclientservice.dto.WithDto;


import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.container.PostContainerDto;

public record UserWithPostsDto(UserDto userDto, PostContainerDto postContainerDto) {
}
