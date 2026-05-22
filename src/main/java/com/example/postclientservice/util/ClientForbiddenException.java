package com.example.postclientservice.util;

public class ClientForbiddenException extends RuntimeException{
    public ClientForbiddenException(String m){
        super(m);
    }
}
