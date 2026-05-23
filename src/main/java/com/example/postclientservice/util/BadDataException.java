package com.example.postclientservice.util;

public class BadDataException  extends  RuntimeException{
    public BadDataException(String message){
        super(message);
    }
}
