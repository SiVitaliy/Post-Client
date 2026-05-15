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

    @GetMapping("/posts")
    @PreAuthorize("hasRole('USER')")
    public String getMainPage(Model model){
        PostContainerDto posts = postClient.getAll();
        model.addAttribute("posts", posts.getPosts());
        return  "main-page";
    }
    @GetMapping("/users")
    @PreAuthorize("hasRole('USER')")
    public String getAllUsers(@RequestParam(required = false) String search ,Model model){
        UserContainerDto userContainerDto = userClient.getAllUsers(search);
        model.addAttribute("users",userContainerDto);
        return "user/all-users-page";
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public String getMyPage(Model model){
        UserWithPostsDto userWithPosts = userClient.getCurrentUserPosts();
        model.addAttribute("currentUserWithPosts",userWithPosts);
        return "/user/my-page";
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('USER')")
    public String getUserPage(@PathVariable int id, Model model){
        if (id == userClient.getCurrentUserFromSession().id()){
            return "redirect:/me";
        }
        UserWithPostsDto userWithPosts = userClient.getUserPosts(id);
        model.addAttribute("user",userWithPosts);
        return "user/user-page";
    }

    @GetMapping("/me/update")
    @PreAuthorize("hasRole('USER') ")
    public String updateCurrentUserPage(Model model){
        model.addAttribute("userToUpdate",userClient.getCurrentUserFromSession());
        return "user/update-my-page";
    }

    @PostMapping("/me/update")
    @PreAuthorize("hasRole('USER')")
    public String updateCurrentUser(@RequestParam(required = false) MultipartFile profilePicture, @ModelAttribute("userToUpdate") UpdateUserRequest request){
       //todo
        userClient.updateCurrentUserProfilePicture(profilePicture);
        userClient.updateCurrentUser(request);
        return "redirect:/me";
    }

    @PostMapping("/me/delete")
    @PreAuthorize("hasRole('USER')")
    private String deleteCurrentUser(){
        userClient.deleteCurrentUser();
        return "redirect:/auth/login";
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
    public String createPost(@RequestParam(required = false) List<MultipartFile> images, @ModelAttribute("post") CreatePostRequest request,Model model){
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


    @PostMapping("/posts/{id}/commentaries")
    @PreAuthorize("hasRole('USER')")
    public String createCommentary(@PathVariable int id, CreateCommentaryRequest request ){
        commentaryClient.save(id,request);
        return "redirect:/posts/"+id;
    }

    @PostMapping("/posts/{postId}/commentaries/{id}")
    @PreAuthorize("hasRole('USER') and @commentaryService.userIsCommentaryAuthor(#id)")
    public String updateCommentary(@PathVariable int postId, @PathVariable int id, @ModelAttribute UpdateCommentaryRequest request){
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
