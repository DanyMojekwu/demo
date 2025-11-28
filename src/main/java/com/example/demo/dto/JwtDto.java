package com.example.demo.dto;

public class JwtDto {
    public String getToken() {
        return token;
    }

    public JwtDto(String token) {
        this.token = token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;

}
