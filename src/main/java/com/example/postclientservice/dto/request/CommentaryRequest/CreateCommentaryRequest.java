package com.example.postclientservice.dto.request.CommentaryRequest;


import com.example.postclientservice.dto.Dto.UserDto;
import jakarta.validation.constraints.NotBlank;

public record CreateCommentaryRequest(@NotBlank String text) {

}
