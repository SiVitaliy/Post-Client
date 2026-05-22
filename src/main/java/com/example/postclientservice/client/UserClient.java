package com.example.postclientservice.client;

import com.example.postclientservice.dto.Dto.JwtResponseDto;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.WithDto.UserWithPostsDto;
import com.example.postclientservice.dto.container.UserContainerDto;
import com.example.postclientservice.dto.request.UserRequest.LoginUserRequest;
import com.example.postclientservice.dto.request.UserRequest.RegisterUserRequest;
import com.example.postclientservice.dto.request.UserRequest.UpdateUserRequest;
import com.example.postclientservice.util.BadLoginException;
import com.example.postclientservice.util.ClientApiException;
import com.example.postclientservice.util.ClientForbiddenException;
import com.example.postclientservice.util.EmailAlreadyExistsException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class UserClient {
    @Value("${post.client.user-url}")
    private  String userUrl;

    @Value("${post.client.registration-url}")
    private  String registerUrl;
    @Value("${post.client.login-url}")
    private String loginUrl;
    @Value("${post.client.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate;
    private final HttpSession session;

    @Autowired
    public UserClient(RestTemplate restTemplate, HttpSession session) {
        this.restTemplate = restTemplate;
        this.session = session;
    }
    private HttpHeaders createAuthHeaders() {
        String token = (String) session.getAttribute("jwt_token");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    public JwtResponseDto register(RegisterUserRequest user) {
        try {
            ResponseEntity<JwtResponseDto> response = restTemplate.postForEntity(
                    registerUrl, user, JwtResponseDto.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (HttpClientErrorException.Conflict ex){
            throw new EmailAlreadyExistsException("Такой email уже зарегистрирован");
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }
    public JwtResponseDto login(LoginUserRequest user){
        try {
            ResponseEntity<JwtResponseDto> response = restTemplate.postForEntity(
                    loginUrl, user, JwtResponseDto.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        }  catch (HttpClientErrorException.Unauthorized ex) {
              throw new BadLoginException("Неравильная почта или пароль");

        } catch (HttpClientErrorException.Forbidden ex) {
             throw new ClientForbiddenException("Нет доступа");

        }
        throw new ClientApiException("Ошибка при обращении к серверу");


    }


    public UserDto getByEmail(String email){
        ResponseEntity<UserDto> response= restTemplate.getForEntity(userUrl+"/"+email, UserDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }

    public UserWithPostsDto getUserPosts(int id){
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<UserWithPostsDto> response = restTemplate.exchange(userUrl+"/id"+id,HttpMethod.GET,entity,UserWithPostsDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }
    public UserWithPostsDto getCurrentUserPosts(){
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<UserWithPostsDto> response = restTemplate.exchange(baseUrl+"/me/posts",
                HttpMethod.GET,entity,UserWithPostsDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }


    public UserDto getCurrentUser(){
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<UserDto> response = restTemplate.exchange(baseUrl+"/me", HttpMethod.GET,
                entity,UserDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }

    public UserDto getCurrentUserFromSession(){
        return (UserDto) session.getAttribute("user");
    }

    public UserDto updateCurrentUser(UpdateUserRequest request) {
        try {
            HttpEntity<?> entity = new HttpEntity<>(request,createAuthHeaders());


            UserDto currentUser = getCurrentUserFromSession();
            ResponseEntity<UserDto> response = restTemplate.exchange(baseUrl+"/me",HttpMethod.PUT,entity, UserDto.class);
            if (response.getStatusCode().is2xxSuccessful()){
                session.setAttribute("user",response.getBody());
                return response.getBody();
            }
        } catch (HttpClientErrorException.Conflict ex){
            throw new EmailAlreadyExistsException("Такой email уже зарегистрирован");
        }
        throw new ClientApiException("Ошибка при обращении к серверу");


    }

    public void deleteCurrentUser() {
        HttpEntity<?> entity =  new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Void> response = restTemplate.exchange(baseUrl+"/me", HttpMethod.DELETE,entity,Void.class);
        if (response.getStatusCode().is2xxSuccessful()){
            session.invalidate();
            SecurityContextHolder.clearContext();
            return;
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }

    public UserContainerDto getAllUsers(String search) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        String searchUrl = "";
        if (search!=null && !search.isBlank()){
            try {
                String encodedSearch = URLEncoder.encode(search, StandardCharsets.UTF_8);
                searchUrl += "?search=" + encodedSearch;
            } catch (Exception e) {
                System.out.println(e.getMessage());
                searchUrl = "";
            }
        }
        ResponseEntity<UserContainerDto> response = restTemplate.exchange(baseUrl+"/users"+searchUrl,HttpMethod.GET,entity,UserContainerDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }

    public UserDto updateCurrentUserProfilePicture(MultipartFile profilePicture) {
        HttpHeaders headers  = createAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        body.add("profilePicture",profilePicture.getResource());

        HttpEntity<?> entity = new HttpEntity<>(body,headers);


        ResponseEntity<UserDto> response = restTemplate.exchange(baseUrl+"/me",HttpMethod.POST,entity,UserDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            session.setAttribute("user",response.getBody());
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }
}
