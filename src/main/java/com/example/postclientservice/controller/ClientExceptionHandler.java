package com.example.postclientservice.controller;


import com.example.postclientservice.util.ClientApiException;
import com.example.postclientservice.util.ClientForbiddenException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ControllerAdvice
public class ClientExceptionHandler {
    @ExceptionHandler(ClientForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleClientForbiddenException(ClientForbiddenException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/403";
    }

//        @ExceptionHandler(ClientNotFoundException.class)
//        @ResponseStatus(HttpStatus.NOT_FOUND)
//        public String handleClientNotFoundException(ClientNotFoundException ex, Model model) {
//            model.addAttribute("message", ex.getMessage());
//            return "error/404";
//        }

        @ExceptionHandler(ClientApiException.class)
        @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        public String handleClientApiException(ClientApiException ex, Model model) {
            model.addAttribute("message", "Сервер временно недоступен");
            return "error/500";
        }
}
