package com.example.demo.dto;

import com.example.demo.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UserDto {

    @JsonProperty(required = true)
    @NotBlank
    private String firstname;
    @JsonProperty(required = true)
    @NotBlank
    private String lastname;
    @JsonProperty(required = true)
    @NotBlank
    private String email;

    private String password;

    @JsonProperty(required = true)
    @NotBlank
    private Role role;



    public UserDto() {
    }

    public @NotBlank String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank String email) {
        this.email = email;
    }

    public @NotBlank String getFirstname() {
        return firstname;
    }

    public void setFirstname(@NotBlank String firstname) {
        this.firstname = firstname;
    }

    public @NotBlank String getLastname() {
        return lastname;
    }

    public void setLastname(@NotBlank String lastname) {
        this.lastname = lastname;
    }

    public @NotBlank Role getRole() {
        return role;
    }

    public void setRole(@NotBlank Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
