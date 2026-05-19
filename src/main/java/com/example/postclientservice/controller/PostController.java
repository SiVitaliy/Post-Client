package com.example.postclientservice.controller;

import com.example.postclientservice.client.CommentaryClient;
import com.example.postclientservice.client.PostClient;
import com.example.postclientservice.client.UserClient;
import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.WithDto.PostWithCommentariesDto;
import com.example.postclientservice.dto.container.PostContainerDto;
import com.example.postclientservice.dto.request.CommentaryRequest.CreateCommentaryRequest;
import com.example.postclientservice.dto.request.PostRequest.CreatePostRequest;
import com.example.postclientservice.dto.request.PostRequest.UpdatePostRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class PostController {
    private  final PostClient postClient ;

    @Autowired
    public PostController(PostClient postClient) {
        this.postClient = postClient;
    }

    @GetMapping("/posts")
    @PreAuthorize("hasRole('USER')")
    public String getMainPage(Model model){
        PostContainerDto posts = postClient.getAll();
        model.addAttribute("posts", posts.getPosts());
        return  "main-page";
    }


    @GetMapping("/posts/{id}")
    @PreAuthorize("hasRole('USER')")
    public String getPostDetailsPage(@PathVariable int id, Model model){
        PostWithCommentariesDto post= postClient.getById(id);
        System.out.println(post.postDto().author()==null);
        model.addAttribute("post",post);
        model.addAttribute("newCommentary",new CreateCommentaryRequest(""));
        return "post/details-page";

    }
    @GetMapping("/posts/create")
    @PreAuthorize("hasRole('USER')")
    public String createPostPage(@ModelAttribute("post") CreatePostRequest request){
//        model.addAttribute("post",new CreatePostRequest(null,null));
        return "/post/create-post-page";
    }

    @PostMapping("/posts/create")
    @PreAuthorize("hasRole('USER')")
    public String createPost(@RequestParam(required = false) List<MultipartFile> images, @ModelAttribute("post") CreatePostRequest request, Model model){
        if (images != null && images.size()>5) {
            model.addAttribute("error", "Можно загрузить не больше 5 картинок");
            model.addAttribute("post", request);
            return "post/create-post-page";
        }
        PostDto postDto = postClient.save(request);

        if (postDto != null && postDto.id() != 0 && images != null && !images.isEmpty()){
            System.out.println("eshkere");

            postClient.addImages(postDto.id(),images);
        }

        return "redirect:/posts";
    }

    @GetMapping("/posts/{id}/update")
    @PreAuthorize("hasRole('USER')")
    public String updatePostPage(@PathVariable int id, Model model){
        PostWithCommentariesDto post= postClient.getById(id);
        model.addAttribute("post",post.postDto());
        model.addAttribute("comments",post.commentaryContainerDto());
        return "/post/update-post-page";
    }
    @PostMapping("/posts/{id}/update")
    @PreAuthorize("hasRole('USER') and @postService.userIsPostAuthor(#id)")
    public String updatePost(@PathVariable int id,
                             @ModelAttribute UpdatePostRequest updatePostRequest){
        postClient.updateById(id, updatePostRequest);
        return "redirect:/posts/"+id;
    }
    @PostMapping("/posts/{id}/delete")
    @PreAuthorize("hasRole('USER') and @postService.userIsPostAuthor(#id)")
    public  String deletePost(@PathVariable int id){
        postClient.deleteById(id);
        return "redirect:/posts";
    }

}
