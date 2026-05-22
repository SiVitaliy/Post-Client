package com.example.postclientservice.controller;

import com.example.postclientservice.client.UserClient;
import com.example.postclientservice.dto.Dto.JwtResponseDto;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.request.UserRequest.LoginUserRequest;
import com.example.postclientservice.dto.request.UserRequest.RegisterUserRequest;
import com.example.postclientservice.util.BadLoginException;
import com.example.postclientservice.util.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AuthController {
    //private final PersonValidator personValidator;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private  final HttpSession session;

    @Autowired
    public AuthController(UserClient userClient, PasswordEncoder passwordEncoder, HttpSession session) {

        this.userClient = userClient;
        this.passwordEncoder = passwordEncoder;
        this.session = session;
    }




    @GetMapping("/login")
    public String loginPage(@ModelAttribute("user") LoginUserRequest loginUserRequest){
        return "/auth/login-page";
    }

    @GetMapping("/registration")
    public String registrationPage(@ModelAttribute("user") RegisterUserRequest user) {
        return "auth/register-page";
    }

    @PostMapping("/process_login")
    public String processLogin(@ModelAttribute("user") @Valid LoginUserRequest loginUserRequest, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()) {
            return "auth/login-page";
        }

        try {
            JwtResponseDto jwt = userClient.login(loginUserRequest);
            if (jwt == null || jwt.getToken() == null || jwt.getToken().isBlank()) {
                model.addAttribute("error", "Ошибка авторизации. Попробуйте позже");
                return "auth/login-page";
            }
            session.setAttribute("jwt_token", jwt.getToken());

            UserDto user = userClient.getCurrentUser();
            session.setAttribute("user", user);

            setAuthorities(user);

            return "redirect:/posts";
        } catch (BadLoginException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/login-page";
        }



    }

    @PostMapping("/perform_registration")
    public String performRegistration(@ModelAttribute("user")@Valid RegisterUserRequest registerUserRequest, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register-page";
        }

        try {
            JwtResponseDto jwt = userClient.register(registerUserRequest);
            if (jwt == null || jwt.getToken() == null || jwt.getToken().isBlank()) {
                model.addAttribute("error", "Ошибка авторизации. Попробуйте позже");
                return "auth/register-page";
            }
            session.setAttribute("jwt_token", jwt.getToken());
            UserDto user = userClient.getCurrentUser();
            session.setAttribute("user", user);
            setAuthorities(user);
            return "redirect:/posts";
        } catch (EmailAlreadyExistsException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register-page";
        }
    }

    private void setAuthorities(UserDto user){
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.role()));

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.email(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }


}