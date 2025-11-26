package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserController {
    UserRepository userRepository;

    @GetMapping("/get")
    public ResponseEntity<List<User>> get (){
        var get =userRepository.findAll();
        return new ResponseEntity<>(get, HttpStatus.OK);

    }
}
