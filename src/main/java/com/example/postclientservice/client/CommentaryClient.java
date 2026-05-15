package com.example.postclientservice.client;

import com.example.postclientservice.dto.Dto.CommentaryDto;
import com.example.postclientservice.dto.Dto.PostDto;
import com.example.postclientservice.dto.request.CommentaryRequest.CreateCommentaryRequest;
import com.example.postclientservice.dto.request.CommentaryRequest.UpdateCommentaryRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CommentaryClient {
    @Value("${post.client.posts-url}")
    private  String postUrl;
    @Value("${post.client.commentaries-url}")
    private String commentaryUrl;
    private final RestTemplate restTemplate;
    private final HttpSession session;

    public CommentaryClient(RestTemplate restTemplate, HttpSession session) {
        this.restTemplate = restTemplate;
        this.session = session;
    }

    private HttpHeaders createAuthHeaders() {
        String token = (String) session.getAttribute("jwt_token");
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }
    public CommentaryDto findById(int id) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<CommentaryDto> response = restTemplate.exchange(commentaryUrl+"/"+id,HttpMethod.GET,entity,CommentaryDto.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Find method failed");
        }

    }

    public CommentaryDto save(int postId, CreateCommentaryRequest request) {
        HttpEntity<?> entity = new HttpEntity<>(request,createAuthHeaders());
        ResponseEntity<CommentaryDto> response  = restTemplate.exchange(postUrl+"/"+postId, HttpMethod.POST,entity,CommentaryDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new RuntimeException("Save method failed");
    }

    public CommentaryDto updateById(int id, UpdateCommentaryRequest request) {
        HttpEntity<?> entity = new HttpEntity<>(request,createAuthHeaders());
        ResponseEntity<CommentaryDto> response = restTemplate.exchange(commentaryUrl+"/"+id, HttpMethod.PUT,entity,CommentaryDto.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return response.getBody();
        }
        throw new RuntimeException("Update method failed");

    }

    public void deleteById(int id) {
        HttpEntity<?> entity = new HttpEntity<>(createAuthHeaders());
        ResponseEntity<Void> response = restTemplate.exchange(commentaryUrl+'/'+id, HttpMethod.DELETE,entity, Void.class);
        if (response.getStatusCode().is2xxSuccessful()){
            return;
        }
        throw new RuntimeException("Delete method failed");
    }
}

