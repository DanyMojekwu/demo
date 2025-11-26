package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserLoginDto {
    @Email ( message = "invalid email")
    @NotBlank(message = "Email required ")
    @JsonProperty(required = true)

    private String email;
    @JsonProperty(required = true)
    private String password;

    public UserLoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public @Email(message = "invalid email") @NotBlank(message = "Email required ") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "invalid email") @NotBlank(message = "Email required ") String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
