package com.example.postclientservice.client;


import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.WithDto.PostWithCommentariesDto;
import com.example.postclientservice.dto.container.PostContainerDto;
import com.example.postclientservice.dto.pageResponse.PageResponse;
import com.example.postclientservice.dto.request.PostRequest.CreatePostRequest;
import com.example.postclientservice.dto.request.PostRequest.UpdatePostRequest;
import com.example.postclientservice.util.ClientApiException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostClient {
    @Value("${post.client.posts-url}")
    private  String postUrl;
    private final RestTemplate restTemplate;
    private final HttpSession session;

    @Autowired
    public PostClient(RestTemplate restTemplate, HttpSession session) {
        this.restTemplate = restTemplate;
        this.session = session;
    }

    private HttpHeaders createAuthHeaders() {
        String token = (String) session.getAttribute("jwt_token");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    public PageResponse<PostDto> getAll(int page){
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());

        String pageRequest = "?page="+page;
        ResponseEntity<PageResponse<PostDto>> response = restTemplate.exchange(
                postUrl+pageRequest,
                HttpMethod.GET,entity,
                new ParameterizedTypeReference<PageResponse<PostDto>>() {}
        );

        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }

    public PostWithCommentariesDto getById(int id,int commentPage){
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        String commentPageRequest = "?commentPage="+commentPage;
        ResponseEntity<PostWithCommentariesDto> response = restTemplate.exchange(postUrl+"/"+id+commentPageRequest, HttpMethod.GET,entity, PostWithCommentariesDto.class);
        System.out.println(response.getBody());
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }


    public  PostDto save(CreatePostRequest request){
        HttpEntity<?> entity = new HttpEntity<>(request,createAuthHeaders());
        ResponseEntity<PostDto> response  = restTemplate.exchange(postUrl, HttpMethod.POST,entity,PostDto.class);
        System.out.println(response.getBody());
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }

    public PostDto updateById(int id, UpdatePostRequest request){
        HttpEntity<?> entity = new HttpEntity<>(request,createAuthHeaders());
        ResponseEntity<PostDto> response = restTemplate.exchange(postUrl+"/"+id, HttpMethod.PUT,entity,PostDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }
    public  void deleteById(int id){
        HttpEntity<?> entity = new HttpEntity<>(id,createAuthHeaders());
        ResponseEntity<Void> response= restTemplate.exchange(postUrl+"/"+id,HttpMethod.DELETE,entity, Void.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return;
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }


    public PostDto addImages(int postId, List<MultipartFile> images) {
        HttpHeaders headers  = createAuthHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String,Object> body = new LinkedMultiValueMap<>();
        //body.add("images",images.stream().map(MultipartFile::getResource).collect(Collectors.toList()));
        for (MultipartFile image:images) {
            body.add("images",image.getResource());

        }
        HttpEntity<?> entity = new HttpEntity<>(body,headers);


        ResponseEntity<PostDto> response = restTemplate.exchange(postUrl+"/"+postId+"/images",HttpMethod.POST,entity,PostDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
              return response.getBody();
        }
        throw new ClientApiException("Ошибка при обращении к серверу");
    }




}
