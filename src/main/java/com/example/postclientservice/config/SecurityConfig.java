package com.example.postclientservice.config;

import com.example.postclientservice.client.UserClient;
import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.Dto.UserDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserClient userClient;

    public SecurityConfig(UserClient userClient) {
        this.userClient = userClient;
    }
    @Bean
    public String MySpnrigBean(){
        return "new bean ";
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        System.out.println("userservice");
//        return username -> {
//            System.out.println("userservice");
//
//            UserDto user = userClient.getByEmail(username);
//            UserDto userc = userClient.getCurrentUserFromSession();
//
//            System.out.println(username);
//            System.out.println(user.email());
//            System.out.println(userc.email());
//
//            //UserDto user = userClient.getCurrentUserFromSession();
//            return org.springframework.security.core.userdetails.User
//                    .builder()
//                   // .username(user.email())
//                    .username(user.email())
//                    .password("")
//                   // .roles(user.role())
//                    .roles(user.role())
//                    .build();
//        };
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/registration",
                                "/error","/auth/process_login",
                                "/auth/perform_registration", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
               // .userDetailsService(userDetailsService())
                .build();
    }
}
