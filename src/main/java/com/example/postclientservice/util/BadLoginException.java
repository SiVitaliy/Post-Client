package com.example.postclientservice.util;

public class BadLoginException extends RuntimeException{
    public BadLoginException(String m){
        super(m);
    }
}
