package com.example.postclientservice.controller;

import com.example.postclientservice.client.CommentaryClient;
import com.example.postclientservice.client.PostClient;

import com.example.postclientservice.client.UserClient;
import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.WithDto.PostWithCommentariesDto;
import com.example.postclientservice.dto.WithDto.UserWithPostsDto;
import com.example.postclientservice.dto.container.PostContainerDto;
import com.example.postclientservice.dto.container.UserContainerDto;
import com.example.postclientservice.dto.request.CommentaryRequest.CreateCommentaryRequest;
import com.example.postclientservice.dto.request.CommentaryRequest.UpdateCommentaryRequest;
import com.example.postclientservice.dto.request.PostRequest.CreatePostRequest;
import com.example.postclientservice.dto.request.PostRequest.UpdatePostRequest;
import com.example.postclientservice.dto.request.UserRequest.UpdateUserRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
public class CommonController {

    private  final  PostClient postClient ;
    private final UserClient userClient;
    private final CommentaryClient commentaryClient;
    private final HttpSession session;
//    private final PostService postService;
//    private final CommentaryService commentaryService;
    @Autowired
    public CommonController(PostClient postClient, UserClient userClient,
                            CommentaryClient commentaryClient,

//                            ,PostService postService,
//                            CommentaryService commentaryService
                            HttpSession session) {
        this.postClient = postClient;
        this.userClient = userClient;
        this.commentaryClient = commentaryClient;
//        this.postService = postService;
//        this.commentaryService = commentaryService;
        this.session = session;
    }

//    @ModelAttribute("currentUser")
//    private UserDto addCurrentUser() {
//        UserDto user = (UserDto) session.getAttribute("user");
//        System.out.println("user from session: " + user);
//        return user;
//    }

    @GetMapping("/")
    @PreAuthorize("hasRole('USER')")
    public String redirectToMainPage(){
        return "redirect:/posts";
    }















}
