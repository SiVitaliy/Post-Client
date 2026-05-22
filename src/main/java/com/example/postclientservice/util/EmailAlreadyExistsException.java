package com.example.postclientservice.util;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String m){
        super(m);
    }
}