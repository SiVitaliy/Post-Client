package com.example.postclientservice.controller;

import com.example.postclientservice.client.CommentaryClient;
import com.example.postclientservice.client.PostClient;
import com.example.postclientservice.client.UserClient;
import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.WithDto.PostWithCommentariesDto;
import com.example.postclientservice.dto.container.PostContainerDto;
import com.example.postclientservice.dto.pageResponse.PageResponse;
import com.example.postclientservice.dto.request.CommentaryRequest.CreateCommentaryRequest;
import com.example.postclientservice.dto.request.PostRequest.CreatePostRequest;
import com.example.postclientservice.dto.request.PostRequest.UpdatePostRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    public String getMainPage(@RequestParam(defaultValue="0")int page, Model model){
        PageResponse<PostDto> postPage = postClient.getAll(page);

        System.out.println(postPage.toString());
        model.addAttribute("page", postPage);
        return  "main-page";
    }


    @GetMapping("/posts/{id}")
    @PreAuthorize("hasRole('USER')")
    public String getPostDetailsPage(@RequestParam(defaultValue = "0")  int commentPage,
            @PathVariable int id, Model model){

        PostWithCommentariesDto post= postClient.getById(id,commentPage);

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
    public String createPost(@RequestParam(required = false) List<MultipartFile> images, @ModelAttribute("post") @Valid CreatePostRequest request,  BindingResult bindingResult,Model model){

        if (bindingResult.hasErrors()) {
            return "/post/create-post-page";
        }
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
    public String updatePostPage(@RequestParam(defaultValue = "0")  int commentPage,
                                 @PathVariable int id, Model model){

        PostWithCommentariesDto post= postClient.getById(id,commentPage);
        model.addAttribute("post",post.postDto());
        model.addAttribute("commentPage",post.commentaryPage());
        model.addAttribute("postToUpdate", new UpdatePostRequest(post.postDto().title(),post.postDto().text()));
        return "/post/update-post-page";
    }
    @PostMapping("/posts/{id}/update")
    @PreAuthorize("hasRole('USER') and @postService.userIsPostAuthor(#id)")
    public String updatePost(@RequestParam(defaultValue = "0")  int commentPage, @PathVariable int id,
                             @ModelAttribute("postToUpdate") @Valid UpdatePostRequest updatePostRequest,BindingResult bindingResult,Model model){
        if (bindingResult.hasErrors()) {
            PostWithCommentariesDto post = postClient.getById(id, commentPage);
            model.addAttribute("post", post.postDto());
            model.addAttribute("commentPage", post.commentaryPage());
            return "post/update-post-page";
        }
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
