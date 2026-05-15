package com.example.postclientservice.dto.Dto;

import java.time.LocalDateTime;

public record CommentaryDto(int id, int postId, UserDto author, LocalDateTime creationDate, String text) {




}
