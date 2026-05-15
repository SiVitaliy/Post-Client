package com.example.postclientservice.controller;
import com.example.postclientservice.dto.Dto.UserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ControllerAdvice;


@ControllerAdvice(assignableTypes = CommonController.class)
public class GlobalControllerAdvice {
    private final HttpSession session;

    public GlobalControllerAdvice(HttpSession session) {
        this.session = session;
    }

    @ModelAttribute("currentUser")
    public UserDto addCurrentUser() {
        UserDto user = (UserDto) session.getAttribute("user");
        System.out.println("user from session: " + user);
        return user;
    }
}
