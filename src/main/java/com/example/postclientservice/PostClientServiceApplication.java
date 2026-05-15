package com.example.postclientservice;

import com.example.postclientservice.dto.Dto.CommentaryDto;
import com.example.postclientservice.dto.Dto.JwtResponseDto;
import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.WithDto.PostWithCommentariesDto;
import com.example.postclientservice.dto.WithDto.UserWithPostsDto;
import com.example.postclientservice.dto.container.PostContainerDto;
import com.example.postclientservice.dto.request.CommentaryRequest.CreateCommentaryRequest;
import com.example.postclientservice.dto.request.CommentaryRequest.UpdateCommentaryRequest;
import com.example.postclientservice.dto.request.PostRequest.CreatePostRequest;
import com.example.postclientservice.dto.request.PostRequest.UpdatePostRequest;
import com.example.postclientservice.dto.request.UserRequest.LoginUserRequest;
import com.example.postclientservice.dto.request.UserRequest.RegisterUserRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
public class PostClientServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostClientServiceApplication.class, args);
    }

}
