package com.example.postclientservice.dto.request.PostRequest;

import jakarta.validation.constraints.NotBlank;

public record CreatePostRequest(@NotBlank String title, @NotBlank String text) {



}
