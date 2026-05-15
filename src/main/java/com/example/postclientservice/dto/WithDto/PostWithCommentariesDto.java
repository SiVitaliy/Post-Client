package com.example.postclientservice.dto.WithDto;


import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.container.CommentaryContainerDto;

public record PostWithCommentariesDto(PostDto postDto, CommentaryContainerDto commentaryContainerDto) {



}
