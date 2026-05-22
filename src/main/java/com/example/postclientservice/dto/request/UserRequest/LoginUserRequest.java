package com.example.postclientservice.dto.request.UserRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserRequest (
        @NotBlank(message = "Введите email")
        @Email(message = "Некорректный email")
        String email,
        String password){
}
