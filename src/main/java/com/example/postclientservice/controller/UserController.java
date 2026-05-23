package com.example.postclientservice.controller;

import com.example.postclientservice.client.CommentaryClient;
import com.example.postclientservice.client.PostClient;
import com.example.postclientservice.client.UserClient;
import com.example.postclientservice.dto.WithDto.UserWithPostsDto;
import com.example.postclientservice.dto.container.UserContainerDto;
import com.example.postclientservice.dto.request.UserRequest.UpdateUserRequest;
import com.example.postclientservice.util.BadDataException;
import com.example.postclientservice.util.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserController {

     private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;

    }


    @GetMapping("/users")
    @PreAuthorize("hasRole('USER')")
    public String getAllUsers(@RequestParam(required = false) String search , Model model){
        UserContainerDto userContainerDto = userClient.getAllUsers(search);
        model.addAttribute("users",userContainerDto);
        return "user/all-users-page";
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public String getMyPage(Model model){
        UserWithPostsDto userWithPosts = userClient.getCurrentUserPosts();
        model.addAttribute("currentUserWithPosts",userWithPosts);
        return "user/my-page";
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
    public String updateCurrentUser(@RequestParam(required = false) MultipartFile profilePicture,
                                    @ModelAttribute("userToUpdate") @Valid UpdateUserRequest request,
                                    BindingResult bindingResult, Model model){
        try {
            if (bindingResult.hasErrors()) {
                  return "user/update-my-page";
            }
            //todo
            if (profilePicture != null && !profilePicture.isEmpty()) {
                userClient.updateCurrentUserProfilePicture(profilePicture);
            }
            String countryCode = request.countryCode();

            if (countryCode != null && countryCode.isBlank()) {    //TODO убрать нормализацию запроса
                                                                    // и нормализировать countryCode на сервере
                countryCode = null;
            }

            UpdateUserRequest normalizedRequest = new UpdateUserRequest(
                    request.fullName(),
                    request.email(),
                    request.yearOfBirth(),
                    countryCode,
                    request.bio()
            );
            userClient.updateCurrentUser(normalizedRequest);

            return "redirect:/me";
        } catch (EmailAlreadyExistsException | BadDataException ex) {
              model.addAttribute("error", ex.getMessage());
            return "user/update-my-page";
        }

    }

    @PostMapping("/me/delete")
    @PreAuthorize("hasRole('USER')")
    private String deleteCurrentUser(){
        userClient.deleteCurrentUser();
        return "redirect:/auth/login";
    }


}
