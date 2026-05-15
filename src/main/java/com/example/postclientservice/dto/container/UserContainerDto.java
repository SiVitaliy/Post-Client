package com.example.postclientservice.dto.container;

import com.example.postclientservice.dto.Dto.UserDto;

import java.util.List;

public class UserContainerDto {
    private final List<UserDto> users;

    public UserContainerDto(List<UserDto> users) {
        this.users = users;
    }

    public List<UserDto> getUsers() {
        return users;
    }
}
