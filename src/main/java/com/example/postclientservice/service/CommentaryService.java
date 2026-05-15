package com.example.postclientservice.service;

import com.example.postclientservice.client.CommentaryClient;
import com.example.postclientservice.dto.Dto.CommentaryDto;
import com.example.postclientservice.dto.Dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentaryService {
    private final HttpSession session;
    private final CommentaryClient commentaryClient;

    @Autowired
    public CommentaryService(HttpSession session, CommentaryClient commentaryClient) {
        this.session = session;
        this.commentaryClient = commentaryClient;
    }

    public boolean userIsCommentaryAuthor(int id){
        try {
            UserDto currentUser = (UserDto) session.getAttribute("user");
            CommentaryDto commentary = commentaryClient.findById(id);
            return currentUser.id()==commentary.author().id();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}
