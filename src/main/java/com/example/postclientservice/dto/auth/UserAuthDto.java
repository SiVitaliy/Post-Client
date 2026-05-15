package com.example.postclientservice.dto.auth;

public class UserAuthDto {
    private  String fullName;
    private final String email;
    private final String password;


    public UserAuthDto(String fullName, String email, String password) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }
    public UserAuthDto( String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
