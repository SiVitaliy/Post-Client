package com.example.postclientservice.client;


import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.Dto.UserDto;
import com.example.postclientservice.dto.WithDto.PostWithCommentariesDto;
import com.example.postclientservice.dto.container.PostContainerDto;
import com.example.postclientservice.dto.request.PostRequest.CreatePostRequest;
import com.example.postclientservice.dto.request.PostRequest.UpdatePostRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

    public PostContainerDto getAll(){
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<PostContainerDto> response = restTemplate.exchange(postUrl, HttpMethod.GET,entity, PostContainerDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new RuntimeException("getAll failed");
    }

    public PostWithCommentariesDto getById(int id){
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<PostWithCommentariesDto> response = restTemplate.exchange(postUrl+"/"+id, HttpMethod.GET,entity, PostWithCommentariesDto.class);
        System.out.println(response.getBody());
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new RuntimeException("getById failed");
    }


    public  PostDto save(CreatePostRequest request){
        HttpEntity<?> entity = new HttpEntity<>(request,createAuthHeaders());
        ResponseEntity<PostDto> response  = restTemplate.exchange(postUrl, HttpMethod.POST,entity,PostDto.class);
        System.out.println(response.getBody());
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new RuntimeException("Save method failed");
    }

    public PostDto updateById(int id, UpdatePostRequest request){
        HttpEntity<?> entity = new HttpEntity<>(request,createAuthHeaders());
        ResponseEntity<PostDto> response = restTemplate.exchange(postUrl+"/"+id, HttpMethod.PUT,entity,PostDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new RuntimeException("Update method failed");
    }
    public  void deleteById(int id){
        HttpEntity<?> entity = new HttpEntity<>(id,createAuthHeaders());
        ResponseEntity<Void> response= restTemplate.exchange(postUrl+"/"+id,HttpMethod.DELETE,entity, Void.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return;
        }
        throw new RuntimeException("Delete method failed");
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
        throw new RuntimeException("addImages failed");
    }




}
