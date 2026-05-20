package com.example.postclientservice.dto.WithDto;


import com.example.postclientservice.dto.Dto.CommentaryDto;
import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.container.CommentaryContainerDto;
import com.example.postclientservice.dto.pageResponse.PageResponse;

public record PostWithCommentariesDto(PostDto postDto, PageResponse<CommentaryDto> commentaryPage) {



}
