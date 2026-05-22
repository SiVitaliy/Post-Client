package com.example.postclientservice.controller;

import com.example.postclientservice.client.CommentaryClient;
import com.example.postclientservice.client.PostClient;
import com.example.postclientservice.client.UserClient;
import com.example.postclientservice.dto.request.CommentaryRequest.CreateCommentaryRequest;
import com.example.postclientservice.dto.request.CommentaryRequest.UpdateCommentaryRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommentaryController {

    private final CommentaryClient commentaryClient;

    @Autowired
    public CommentaryController( CommentaryClient commentaryClient) {
        this.commentaryClient = commentaryClient;
    }


    @PostMapping("/posts/{id}/commentaries")
    @PreAuthorize("hasRole('USER')")
    public String createCommentary(@PathVariable int id,  @ModelAttribute  CreateCommentaryRequest request, BindingResult bindingResult){

        commentaryClient.save(id,request);
        return "redirect:/posts/"+id;
    }

    @PostMapping("/posts/{postId}/commentaries/{id}")
    @PreAuthorize("hasRole('USER') and @commentaryService.userIsCommentaryAuthor(#id)")
    public String updateCommentary(@PathVariable int postId, @PathVariable int id,
                                    @ModelAttribute UpdateCommentaryRequest request,
                                   BindingResult bindingResult){

        commentaryClient.updateById(id,request);
        return "redirect:/posts/"+postId;
    }

    @PostMapping("/posts/{postId}/commentaries/{id}/delete")
    @PreAuthorize("hasRole('USER') and (@commentaryService.userIsCommentaryAuthor(#id) or @postService.userIsPostAuthor(#postId))")
    public String deleteCommentary(@PathVariable int postId, @PathVariable int id){

        commentaryClient.deleteById(id);
        return "redirect:/posts/"+postId;
    }
}
