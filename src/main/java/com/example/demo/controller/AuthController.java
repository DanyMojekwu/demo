package com.example.demo.controller;

import com.example.demo.dto.UserDto;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@RequestBody UserDto user){
        System.out.println(user.getPassword());
       User newUser=new User(
               user.getEmail(),
               user.getLastname(),
               user.getFirstname(),
               bCryptPasswordEncoder.encode(user.getPassword()),
               user.getRole());


       var save =userRepository.save(newUser);

       return new ResponseEntity<>(user, HttpStatus.CREATED);

    }

}
