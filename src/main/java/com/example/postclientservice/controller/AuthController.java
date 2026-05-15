package com.example.postclientservice.controller;

import com.example.postclientservice.client.UserClient;
import com.example.postclientservice.dto.Dto.JwtResponseDto;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.request.UserRequest.LoginUserRequest;
import com.example.postclientservice.dto.request.UserRequest.RegisterUserRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
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
    public String processLogin(@ModelAttribute("user") LoginUserRequest loginUserRequest){

        JwtResponseDto jwt = userClient.login(loginUserRequest);
         if (jwt == null){
            return "redirect:/auth/login?error";
        }
        session.setAttribute("jwt_token", jwt.getToken());

        UserDto user = userClient.getCurrentUser();
        session.setAttribute("user", user);
        System.out.println(user.email());
        System.out.println(user.role());
        setAuthorities(user);

        return "redirect:/posts";
    }

    @PostMapping("/perform_registration")
    public String performRegistration(@ModelAttribute("user") RegisterUserRequest registerUserRequest, BindingResult bindingResult) {
        System.out.println("qwerty");
        JwtResponseDto jwt = userClient.register(registerUserRequest);
        session.setAttribute("jwt_token", jwt.getToken());
        UserDto user = userClient.getCurrentUser();
        session.setAttribute("user", user);
        setAuthorities(user);
        return "redirect:/posts";
    }

    private void setAuthorities(UserDto user){
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.role()));

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(user.email(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
    }


}