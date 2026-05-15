package com.example.postclientservice.service;

import com.example.postclientservice.client.PostClient;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.WithDto.PostWithCommentariesDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    private final PostClient postClient;
    private final HttpSession session;

    public PostService(PostClient postClient, HttpSession session) {
        this.postClient = postClient;
        this.session = session;
    }

    public boolean userIsPostAuthor(int postId){
        try {
            PostWithCommentariesDto post =postClient.getById(postId); //TODO замменить пост с коментами на потс дто
            UserDto currentUser = (UserDto) session.getAttribute("user");
            return post.postDto().author().id()==currentUser.id();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}