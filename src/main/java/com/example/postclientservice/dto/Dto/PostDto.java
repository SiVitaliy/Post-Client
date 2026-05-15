package com.example.postclientservice.dto.Dto;

import com.example.postclientservice.dto.container.PostImageContainerDto;

import java.time.LocalDateTime;

public record PostDto(int id, UserDto author, LocalDateTime creationDate, int numberOfLikes, int numberOfDislikes,
                      String title, String text, PostImageContainerDto postImageContainerDto) {




}